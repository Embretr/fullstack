<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useGetItemById, useIsItemFavorited, useAddToFavorites, useRemoveFromFavorites } from '../api/item-management/item-management';
import { computed, onMounted } from 'vue';
import type { ItemResponseDTO } from '../api/model/itemResponseDTO';
import { useAuthStore } from '@/stores/auth';
import { RouterLink } from 'vue-router';

const route = useRoute();
const itemId = computed(() => route.params.id as string);
const { data: itemData } = useGetItemById(Number(itemId.value));
const authStore = useAuthStore();
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Type assertion for the item data
const typedItemData = computed(() => itemData.value?.data as ItemResponseDTO | undefined);

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
        <img 
          v-for="image in typedItemData.imageUrls" 
          :key="image" 
          :src="image" 
          :alt="typedItemData.title"
          class="gallery-image"
        />
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
          <button 
            v-if="isAuthenticated" 
            @click="toggleFavorite" 
            :class="['favorite-button', { 'is-favorite': isFavorited }]"
          >
            {{ isFavorited ? 'Remove from Favorites' : 'Add to Favorites' }}
          </button>
          <RouterLink 
            v-if="isAuthenticated && typedItemData.owner" 
            :to="{ name: 'Chat', params: { itemId: itemId, receiverId: typedItemData.owner.id } }"
            class="chat-button"
          >
            Chat with Seller
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
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.gallery-image {
  width: 100%;
  height: auto;
  border-radius: var(--border-radius);
  object-fit: cover;
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

.favorite-button {
  margin-top: 2rem;
  padding: 0.75rem 1.5rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  text-decoration: none;
  display: inline-block;
}

.favorite-button:hover {
  background-color: var(--secondary-color-hover);
}

.favorite-button.is-favorite {
  background-color: #ff4444;
}

.favorite-button.is-favorite:hover {
  background-color: #cc0000;
}

.chat-button {
  margin-top: 2rem;
  padding: 0.75rem 1.5rem;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  text-decoration: none;
  display: inline-block;
}

.chat-button:hover {
  background-color: var(--primary-color-hover);
}

.action-buttons {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .item-container {
    grid-template-columns: 1fr;
  }
}
</style> 