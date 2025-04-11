<template>
  <Card class="item-card" :clickable="true" @click="$emit('click', item)">
    <template #header>
      <div class="item-image-container">
        <img
          :src="getImageUrl(item.imageUrls?.[0] || '')"
          :alt="item.title"
          class="item-image"
        />
      </div>
    </template>
    <div class="item-content">
      <h3 class="item-title">{{ item.title }}</h3>
      <p class="item-description">{{ item.briefDescription || 'No description available' }}</p>
      <p class="item-price">{{ item.price }} kr</p>
    </div>
    <template #footer v-if="$slots.footer">
      <slot name="footer"></slot>
    </template>
  </Card>
</template>

<script setup lang="ts">
import Card from './Card.vue'
import { getImageUrl } from '@/utils/imageUtils'

interface Item {
  id?: number
  title: string
  briefDescription?: string
  price: number
  imageUrls?: string[]
}

defineProps<{
  item: Item
}>()

defineEmits<{
  click: (item: Item) => void
}>()
</script>

<style scoped>
.item-card {
  width: 100%;
  max-width: 300px;
}

.item-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: var(--border-radius) var(--border-radius) 0 0;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  padding: 1rem;
}

.item-title {
  font-size: 1.2rem;
  margin: 0 0 0.5rem 0;
  color: var(--text-color);
}

.item-description {
  font-size: 0.9rem;
  color: var(--text-color-light);
  margin: 0 0 0.5rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-price {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--primary-color);
  margin: 0;
}
</style> 