<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useGetItemById, useIsItemFavorited, useAddToFavorites, useRemoveFromFavorites, useReserveItem, useCancelReservation } from '../api/item-management/item-management';
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import type { ItemResponseDTO } from '../api/model/itemResponseDTO';
import { useAuthStore } from '@/stores/auth';
import { RouterLink } from 'vue-router';
import Button from '@/components/common/Button.vue';
import { getImageUrls } from '@/utils/imageUtils';

const route = useRoute();
const itemId = computed(() => route.params.id as string);
const { data: itemData, refetch: refetchItem } = useGetItemById(Number(itemId.value));
const authStore = useAuthStore();
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Type assertion for the item data
const typedItemData = computed<ItemResponseDTO | undefined>(() => {
  if (!itemData.value?.data) return undefined;
  
  const item = { ...itemData.value.data };
  // Construct full image URLs
  if (item.imageUrls && Array.isArray(item.imageUrls)) {
    item.imageUrls = getImageUrls(item.imageUrls);
  }
  return item;
});

// Carousel state
const currentImageIndex = ref(0);

const nextImage = () => {
  if (typedItemData.value?.imageUrls) {
    currentImageIndex.value = (currentImageIndex.value + 1) % typedItemData.value.imageUrls.length;
  }
};

const prevImage = () => {
  if (typedItemData.value?.imageUrls) {
    currentImageIndex.value = (currentImageIndex.value - 1 + typedItemData.value.imageUrls.length) % typedItemData.value.imageUrls.length;
  }
};

// Use the generated API client for favorite status
const { data: favoriteData, refetch: refetchFavoriteStatus } = useIsItemFavorited(Number(itemId.value));
const isFavorited = computed(() => favoriteData.value?.data ?? false);

// Use the generated API client for adding/removing favorites
const { mutate: addToFavorites } = useAddToFavorites({
  mutation: {
    onSuccess: () => {
      refetchFavoriteStatus();
    }
  }
});

const { mutate: removeFromFavorites } = useRemoveFromFavorites({
  mutation: {
    onSuccess: () => {
      refetchFavoriteStatus();
    }
  }
});

const { mutate: reserveItem } = useReserveItem({
  mutation: {
    onSuccess: () => {
      console.log('Reservation successful');
      refetchItem();
    },
    onError: (error) => {
      console.error('Reservation failed:', error);
    }
  }
});

const { mutate: cancelReservation } = useCancelReservation({
  mutation: {
    onSuccess: () => {
      console.log('Cancellation successful');
      refetchItem();
    },
    onError: (error) => {
      console.error('Cancellation failed:', error);
    }
  }
});

const toggleFavorite = () => {
  if (!isAuthenticated.value) return;
  
  if (isFavorited.value) {
    removeFromFavorites({ itemId: Number(itemId.value) });
  } else {
    addToFavorites({ itemId: Number(itemId.value) });
  }
};

const handleReservation = () => {
  if (!isAuthenticated.value) {
    console.log('User not authenticated');
    return;
  }
  
  console.log('Current item status:', typedItemData.value?.status);
  console.log('Current user ID:', authStore.user?.id);
  console.log('Reserved by ID:', typedItemData.value?.reservedBy?.id);
  
  if (typedItemData.value?.status === 'RESERVED' && typedItemData.value?.reservedBy?.id === authStore.user?.id) {
    console.log('Cancelling reservation');
    cancelReservation({ itemId: Number(itemId.value) });
  } else {
    console.log('Reserving item');
    reserveItem({ itemId: Number(itemId.value) });
  }
};

const isReserved = computed(() => typedItemData.value?.status === 'RESERVED');
const isReservedByCurrentUser = computed(() => 
  isReserved.value && 
  typedItemData.value?.reservedBy?.id === authStore.user?.id
);

// Add a force update ref
const forceUpdate = ref(0);

const reservationTimeLeft = computed(() => {
  // Add forceUpdate to dependencies
  forceUpdate.value;
  
  if (!isReserved.value || !typedItemData.value?.reservationDate) return null;
  
  const reservationDate = new Date(typedItemData.value.reservationDate);
  const expirationDate = new Date(reservationDate.getTime() + 60 * 60 * 1000); // 1 hour
  const now = new Date();
  
  if (now >= expirationDate) {
    // If reservation has expired, refetch the item to update its status
    refetchItem();
    return null;
  }
  
  const diff = expirationDate.getTime() - now.getTime();
  const minutes = Math.floor(diff / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);
  
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
});

// Add a timer to update the countdown every second
let timer: number | null = null;

const startTimer = () => {
  if (timer !== null) {
    clearInterval(timer);
  }
  timer = window.setInterval(() => {
    // Force a re-computation by updating the forceUpdate ref
    forceUpdate.value++;
  }, 1000);
};

const stopTimer = () => {
  if (timer !== null) {
    clearInterval(timer);
    timer = null;
  }
};

// Watch for changes in reservation status
watch(isReserved, (newValue) => {
  if (newValue) {
    startTimer();
  } else {
    stopTimer();
  }
});

onMounted(() => {
  if (isAuthenticated.value) {
    refetchFavoriteStatus();
  }
  
  // Start the timer if the item is reserved
  if (isReserved.value) {
    startTimer();
  }
});

onUnmounted(() => {
  stopTimer();
});
</script>

<template>
  <div class="item-view" v-if="typedItemData">
    <div class="item-container">
      <!-- Image Gallery -->
      <div class="image-gallery">
        <div class="carousel-container">
          <button class="carousel-button prev" @click="prevImage" :disabled="!typedItemData.imageUrls || typedItemData.imageUrls.length <= 1">
            &lt;
          </button>
          <div class="carousel">
            <transition name="fade" mode="out-in">
              <img 
                v-if="typedItemData.imageUrls && typedItemData.imageUrls.length > 0"
                :key="currentImageIndex"
                :src="typedItemData.imageUrls[currentImageIndex]" 
                :alt="typedItemData.title"
                class="gallery-image"
              />
              <div v-else class="no-image">
                {{ $t('itemView.noImage') }}
              </div>
            </transition>
          </div>
          <button class="carousel-button next" @click="nextImage" :disabled="!typedItemData.imageUrls || typedItemData.imageUrls.length <= 1">
            &gt;
          </button>
          <div class="carousel-indicators" v-if="typedItemData.imageUrls && typedItemData.imageUrls.length > 1">
            <button
              v-for="(_, index) in typedItemData.imageUrls"
              :key="index"
              class="indicator"
              :class="{ active: currentImageIndex === index }"
              @click="currentImageIndex = index"
            />
          </div>
        </div>
      </div>

      <!-- Item Details -->
      <div class="item-details">
        <h1>{{ typedItemData.title }}</h1>
        <p class="price">{{ typedItemData.price }} kr</p>
        <div class="description">
          <h3>{{ $t('itemView.description') }}</h3>
          <p>{{ typedItemData.fullDescription }}</p>
        </div>
        <div class="seller-info">
          <h3>{{ $t('itemView.sellerInfo') }}</h3>
          <p>{{ $t('itemView.postedBy', { username: typedItemData.owner?.username || $t('itemView.anonymous') }) }}</p>
        </div>
        <div class="reservation-info" v-if="isReserved">
          <p class="reservation-status">
            {{ isReservedByCurrentUser ? $t('itemView.reservedByYou') : $t('itemView.reservedByOther') }}
            <span v-if="reservationTimeLeft">{{ $t('itemView.timeRemaining', { time: reservationTimeLeft }) }}</span>
          </p>
        </div>
        <div class="action-buttons">
          <Button 
            v-if="isAuthenticated" 
            @click="toggleFavorite" 
            :variant="isFavorited ? 'secondary' : 'primary'"
            size="large"
          >
            {{ isFavorited ? $t('itemView.removeFromFavorites') : $t('itemView.addToFavorites') }}
          </Button>
          <Button 
            v-if="isAuthenticated && !isReserved" 
            @click="handleReservation" 
            variant="primary"
            size="large"
          >
            {{ $t('itemView.reserveItem') }}
          </Button>
          <Button 
            v-if="isReservedByCurrentUser" 
            @click="handleReservation" 
            variant="secondary"
            size="large"
          >
            {{ $t('itemView.cancelReservation') }}
          </Button>
          <RouterLink 
            v-if="isAuthenticated && typedItemData.owner" 
            :to="{ 
              name: 'Chat', 
              params: { 
                itemId: itemId, 
                receiverId: authStore.user?.id
              } 
            }"
          >
            <Button variant="primary" size="large">
              {{ $t('itemView.chatWithSeller') }}
            </Button>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    {{ $t('itemView.loading') }}
  </div>
</template>

<style scoped>
.item-view {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.item-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.image-gallery {
  position: relative;
}

.carousel-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
}

.carousel {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--border-radius);
}

.carousel-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: background-color 0.2s;
}

.carousel-button:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.carousel-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.carousel-button.prev {
  left: 10px;
}

.carousel-button.next {
  right: 10px;
}

.carousel-indicators {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 2;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.indicator.active {
  background-color: white;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.item-details {
  padding: 1rem;
}

.price {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary-color);
  margin: 1rem 0;
}

.description, .seller-info {
  margin-top: 2rem;
}

h3 {
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: var(--text-secondary);
}

.reservation-info {
  margin-top: 1rem;
  padding: 1rem;
  background-color: var(--background-secondary);
  border-radius: var(--border-radius);
}

.reservation-status {
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin: 0;
}

.action-buttons {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.favorite-button,
.chat-button {
  display: none;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--background-secondary);
  color: var(--text-secondary);
  font-size: 1.2rem;
  border-radius: var(--border-radius);
}

@media (max-width: 768px) {
  .item-container {
    grid-template-columns: 1fr;
  }
}
</style> 