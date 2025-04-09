<template>
  <div class="favorites">
    <h1>{{ $t('favoritesView.title') }}</h1>
    <div v-if="!favorites.length" class="empty-state">
      <p>{{ $t('favoritesView.emptyState') }}</p>
    </div>
    <div v-else class="favorites-grid">
      <div v-for="item in favorites" :key="item.id" class="favorite-card">
        <img v-if="item.imageUrls && item.imageUrls.length > 0" :src="item.imageUrls[0]" :alt="item.title" class="item-image">
        <h2>{{ item.title }}</h2>
        <p class="price">${{ item.price }}</p>
        <p class="description">{{ item.briefDescription }}</p>
        <button @click="removeFromFavorites(item.id)" class="remove-button">
          {{ $t('favoritesView.remove') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useGetUserFavorites, useRemoveFromFavorites } from '@/api/item-management/item-management';
import type { ItemResponseDTO } from '@/api/model';

const authStore = useAuthStore();
const favorites = ref<ItemResponseDTO[]>([]);

const { data: favoritesData, refetch: refetchFavorites } = useGetUserFavorites();

const { mutate: removeFavorite } = useRemoveFromFavorites({
  mutation: {
    onSuccess: () => {
      refetchFavorites();
    }
  }
});

const removeFromFavorites = (itemId: number | undefined) => {
  if (itemId) {
    removeFavorite({ itemId });
  }
};

onMounted(() => {
  if (authStore.isAuthenticated) {
    refetchFavorites();
  }
});

// Watch for changes in favoritesData
watch(favoritesData, (newData) => {
  if (newData?.data) {
    favorites.value = Array.isArray(newData.data) ? newData.data : [];
  }
}, { immediate: true });
</script>

<style scoped>
.favorites {
  padding: 2rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: var(--border-radius);
  margin-top: 2rem;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.favorite-card {
  background: white;
  padding: 1.5rem;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.item-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: var(--border-radius);
}

h1 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

h2 {
  color: var(--secondary-color);
  margin-bottom: 0.5rem;
}

.price {
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.description {
  color: #666;
}

.remove-button {
  background-color: #ff4444;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: background-color 0.3s;
}

.remove-button:hover {
  background-color: #cc0000;
}
</style> 