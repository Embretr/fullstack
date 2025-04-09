<template>
  <div class="category-items">
    <h1 v-if="categoryData?.data?.name">{{ categoryData.data.name }}</h1>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-else class="items-grid">
      <div v-for="item in itemsData?.data" :key="item.id" class="item-card">
        <router-link :to="'/items/' + item.id">
          <div class="item-image" v-if="item.imageUrls && item.imageUrls.length > 0">
            <img :src="item.imageUrls[0]" :alt="item.title">
          </div>
          <div class="item-details">
            <h2>{{ item.title }}</h2>
            <p class="price">{{ $t('common.price', { price: item.price }) }}</p>
            <p class="description">{{ item.briefDescription }}</p>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useGetItemsByCategory } from '@/api/item-management/item-management';
import { useGetCategoryById } from '@/api/category-management/category-management';

const route = useRoute();
const categoryId = computed(() => Number(route.params.id));

// Get items by category
const { data: itemsData, isLoading, error } = useGetItemsByCategory(categoryId);

// Get category details
const { data: categoryData, isLoading: categoryLoading, error: categoryError } = useGetCategoryById(categoryId);
</script>

<style scoped>
.category-items {
  padding: 2rem;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.item-card {
  background: white;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  overflow: hidden;
}

.item-card:hover {
  transform: translateY(-5px);
}

.item-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-details {
  padding: 1rem;
}

.item-details h2 {
  margin: 0 0 0.5rem 0;
  color: var(--primary-color);
}

.price {
  font-weight: bold;
  color: var(--secondary-color);
  margin: 0 0 0.5rem 0;
}

.description {
  color: var(--text-color);
  margin: 0;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: var(--error-color);
}
</style> 