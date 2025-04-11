<script setup lang="ts">
import { getImageUrl } from '@/utils/imageUtils';
import type { ItemResponseDTO } from '@/api/model/itemResponseDTO';

defineProps<{
  item: ItemResponseDTO;
}>();
</script>

<template>
  <div class="item-card">
    <img :src="getImageUrl(item.imageUrls?.[0] || '')" :alt="item.title" class="item-image">
    <div class="item-details">
      <h3>{{ item.title }}</h3>
      <p>{{ item.briefDescription }}</p>
      <div class="item-meta">
        <span class="price">${{ item.price }}</span>
        <span class="category">{{ item.category?.name }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.item-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.item-card:hover {
  transform: translateY(-2px);
}

.item-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.item-details {
  padding: 1rem;
}

.item-details h3 {
  margin: 0 0 0.5rem;
  font-size: 1.2rem;
}

.item-details p {
  margin: 0 0 1rem;
  color: #666;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-weight: bold;
  color: #2c3e50;
}

.category {
  color: #666;
  font-size: 0.9rem;
}
</style> 