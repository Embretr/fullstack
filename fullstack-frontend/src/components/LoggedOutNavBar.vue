<template>
  <nav class="navbar">
    <RouterLink to="/login">
      <Button variant="primary" size="medium">{{ $t('navbar.login') }}</Button>
    </RouterLink>
    <RouterLink to="/register">
      <Button variant="outline" size="medium">{{ $t('navbar.register') }}</Button>
    </RouterLink>
  </nav>
</template>

<script setup lang="ts">
import Button from './common/Button.vue';
// No script logic needed for translations here, but could import useI18n if required later
</script>

<style scoped>
.navbar {
  display: flex;
  gap: 1rem;
}


</style>