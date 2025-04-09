<template>
  <div class="categories">
    <h1>{{ $t('categoriesView.title') }}</h1>
    <div v-if="isLoading" class="loading">Loading...</div>
    <div v-else-if="error" class="error">Error loading categories</div>
    <div v-else class="categories-grid">
      <router-link v-for="category in categoriesData?.data" :key="category.id" :to="'/categories/' + category.id" class="category-card">
        <h2>{{ category.name }}</h2>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGetAllCategories } from '@/api/category-management/category-management';


const { data:categoriesData, isLoading, error } = useGetAllCategories();


</script>

<style scoped>
.categories {
  padding: 2rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.category-card {
  background: white;
  padding: 1.5rem;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  text-decoration: none;
  color: inherit;
}

.category-card:hover {
  transform: translateY(-5px);
}

h1 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

h2 {
  color: var(--secondary-color);
  margin-bottom: 0.5rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  color: var(--secondary-color);
}
</style> 