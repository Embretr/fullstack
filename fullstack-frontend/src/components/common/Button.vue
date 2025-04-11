<template>
  <button
    :class="[
      'button',
      variant,
      size,
      { 'full-width': fullWidth }
    ]"
    :type="type"
    :disabled="disabled"
    @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
defineProps<{
  variant?: 'primary' | 'secondary' | 'outline' | 'text'
  size?: 'small' | 'medium' | 'large'
  type?: 'button' | 'submit' | 'reset'
  disabled?: boolean
  fullWidth?: boolean
}>()

defineEmits<{
  click: (event: MouseEvent) => void
}>()
</script>

<style scoped>
.button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.button.full-width {
  width: 100%;
}

/* Variants */
.primary {
  background-color: var(--primary-color);
  color: white;
}

.primary:hover {
  background-color: var(--primary-color-dark);
}

.secondary {
  background-color: var(--secondary-color);
  color: white;
}

.secondary:hover {
  background-color: var(--secondary-color-dark);
}

.outline {
  background-color: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
}

.outline:hover {
  background-color: var(--primary-color-light);
}

.text {
  background-color: transparent;
  color: var(--primary-color);
  padding: 0;
}

.text:hover {
  text-decoration: underline;
}

/* Sizes */
.small {
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
}

.medium {
  font-size: 1rem;
  padding: 0.5rem 1rem;
}

.large {
  font-size: 1.125rem;
  padding: 0.75rem 1.5rem;
}

.button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style> 