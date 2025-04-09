<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useGetItemById } from '../api/item-management/item-management';
import { computed } from 'vue';

const route = useRoute();
const itemId = computed(() => route.params.id as string);
const { data: itemData } = useGetItemById(itemId.value);
</script>

<template>
  <div class="item-view" v-if="itemData?.data">
    <div class="item-container">
      <!-- Image Gallery -->
      <div class="image-gallery">
        <img 
          v-for="image in itemData.data.images" 
          :key="image.id" 
          :src="image.imageUrl" 
          :alt="itemData.data.title"
          class="gallery-image"
        />
      </div>

      <!-- Item Details -->
      <div class="item-details">
        <h1>{{ itemData.data.title }}</h1>
        <p class="price">${{ itemData.data.price }}</p>
        <div class="description">
          <h3>Description</h3>
          <p>{{ itemData.data.fullDescription }}</p>
        </div>
        <div class="seller-info">
          <h3>Seller Information</h3>
          <p>Posted by: {{ itemData.data.user?.username || 'Anonymous' }}</p>
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

@media (max-width: 768px) {
  .item-container {
    grid-template-columns: 1fr;
  }
}
</style> 