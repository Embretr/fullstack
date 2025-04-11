<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useGetItemById, useIsItemFavorited, useAddToFavorites, useRemoveFromFavorites } from '../api/item-management/item-management';
import { computed, onMounted, ref } from 'vue';
import type { ItemResponseDTO } from '../api/model/itemResponseDTO';
import { useAuthStore } from '@/stores/auth';
import { RouterLink } from 'vue-router';
import Button from '@/components/common/Button.vue';

const route = useRoute();
const itemId = computed(() => route.params.id as string);
const { data: itemData } = useGetItemById(Number(itemId.value));
const authStore = useAuthStore();
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Type assertion for the item data
const typedItemData = computed(() => itemData.value?.data as ItemResponseDTO | undefined);

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

const toggleFavorite = () => {
  if (!isAuthenticated.value) return;
  
  if (isFavorited.value) {
    removeFromFavorites({ itemId: Number(itemId.value) });
  } else {
    addToFavorites({ itemId: Number(itemId.value) });
  }
};

onMounted(() => {
  if (isAuthenticated.value) {
    refetchFavoriteStatus();
  }
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
                :key="currentImageIndex"
                :src="typedItemData?.imageUrls?.[currentImageIndex]" 
                :alt="typedItemData?.title"
                class="gallery-image"
              />
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
        <p class="price">${{ typedItemData.price }}</p>
        <div class="description">
          <h3>Description</h3>
          <p>{{ typedItemData.fullDescription }}</p>
        </div>
        <div class="seller-info">
          <h3>Seller Information</h3>
          <p>Posted by: {{ typedItemData.owner?.username || 'Anonymous' }}</p>
        </div>
        <div class="action-buttons">
          <Button 
            v-if="isAuthenticated" 
            @click="toggleFavorite" 
            :variant="isFavorited ? 'secondary' : 'primary'"
            size="large"
          >
            {{ isFavorited ? 'Remove from Favorites' : 'Add to Favorites' }}
          </Button>
          <RouterLink 
            v-if="isAuthenticated && typedItemData.owner" 
            :to="{ 
              name: 'Chat', 
              params: { 
                itemId: itemId, 
                receiverId: typedItemData.owner.id,
                sellerId: typedItemData.owner.id,
                itemPrice: typedItemData.price,
                itemTitle: typedItemData.title
              } 
            }"
          >
            <Button variant="primary" size="large">
              Chat with Seller
            </Button>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    Loading item details...
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

@media (max-width: 768px) {
  .item-container {
    grid-template-columns: 1fr;
  }
}
</style> 