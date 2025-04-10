<script setup lang="ts">
import HomeComponent from '@/components/HomeComponent.vue';
</script>

<template>
  <HomeComponent />
</template>

<style scoped>
.home {
  padding: 2rem;
  text-align: center;
}

h1 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.header {
  background-color: var(--header-bg-color);
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.main-nav {
  background-color: aqua;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  margin: auto;
}
</style>