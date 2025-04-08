<template>
  <div class="categories">
    <h1>{{ $t('categoriesView.title') }}</h1>
    <div class="categories-grid">
      <div v-for="category in categories" :key="category.id" class="category-card">
        <h2>{{ category.name }}</h2>
        <p>{{ category.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

interface Category {
  id: number;
  name: string;
  description: string;
}

const categories: Category[] = [
  { id: 1, name: t('categoriesView.categoryElectronicsName'), description: t('categoriesView.categoryElectronicsDesc') },
  { id: 2, name: t('categoriesView.categoryClothingName'), description: t('categoriesView.categoryClothingDesc') },
  { id: 3, name: t('categoriesView.categoryHomeGardenName'), description: t('categoriesView.categoryHomeGardenDesc') },
  { id: 4, name: t('categoriesView.categorySportsName'), description: t('categoriesView.categorySportsDesc') }
];
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
</style> 