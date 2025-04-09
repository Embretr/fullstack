<template>
  <div class="favorites">
    <h1>{{ $t('favoritesView.title') }}</h1>
    <div v-if="favorites.length === 0" class="empty-state">
      <p>{{ $t('favoritesView.emptyState') }}</p>
    </div>
    <div v-else class="favorites-grid">
      <div v-for="item in favorites" :key="item.id" class="favorite-card">
        <h2>{{ item.name }}</h2>
        <p class="price">${{ item.price }}</p>
        <p class="description">{{ item.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface FavoriteItem {
  id: number;
  name: string;
  price: number;
  description: string;
}

const favorites: FavoriteItem[] = [];
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
</style> 