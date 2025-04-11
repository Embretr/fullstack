<template>
  <div class="category-items">
    <h1 v-if="categoryData?.data?.name">{{ categoryData.data.name }}</h1>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-else class="items-grid">
      <ItemCard
        v-for="item in itemsData?.data"
        :key="item.id"
        :item="item"
        @click="router.push(`/item/${item.id}`)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useGetItemsByCategory } from '@/api/item-management/item-management';
import { useGetCategoryById } from '@/api/category-management/category-management';
import ItemCard from '@/components/common/ItemCard.vue';

const route = useRoute();
const router = useRouter();
const categoryId = computed(() => Number(route.params.id));

// Get items by category
const { data: itemsData, error } = useGetItemsByCategory(categoryId);

// Get category details
const { data: categoryData } = useGetCategoryById(categoryId);
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

.error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: var(--error-color);
}
</style> 