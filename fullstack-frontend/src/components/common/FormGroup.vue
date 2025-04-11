<template>
  <div class="form-group" :class="{ 'has-error': error }">
    <label v-if="label" :for="id" class="form-label">{{ label }}</label>
    <div class="form-control">
      <slot></slot>
    </div>
    <span v-if="error" class="error-message">{{ error }}</span>
    <span v-if="helpText" class="help-text">{{ helpText }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  id?: string
  label?: string
  error?: string
  helpText?: string
}>()
</script>

<style scoped>
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.form-label {
  color: var(--text-color);
  font-weight: 500;
  font-size: 0.875rem;
}

.form-control {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.error-message {
  color: var(--error-color);
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.help-text {
  color: var(--text-color-light);
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.has-error .form-label {
  color: var(--error-color);
}
</style> 