<template>
  <div class="card" :class="{ 'clickable': clickable }" @click="clickable ? $emit('click') : null">
    <div v-if="$slots.header" class="card-header">
      <slot name="header"></slot>
    </div>
    <div class="card-content">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  clickable?: boolean
}>()

defineEmits<{
  click: () => void
}>()
</script>

<style scoped>
.card {
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card.clickable {
  cursor: pointer;
}

.card.clickable:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.card-header {
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.card-content {
  margin: 1rem 0;
}

.card-footer {
  margin-top: 1rem;
  padding-top: 0.5rem;
  border-top: 1px solid var(--border-color);
}
</style> 