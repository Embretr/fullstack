<script setup lang="ts">
import RegisterForm from '@/components/RegisterForm.vue'
</script>

<template>
  <div class="register">
    <div class="register-container">
      <h1>{{ $t('navbar.register') }}</h1>
      <RegisterForm />
    </div>
  </div>
</template>

<style scoped>
/* Add styles if needed */
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px); /* Adjust based on header/footer */
  padding: 2rem;
}

.register-container {
  background: white;
  padding: 2rem;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px; /* Slightly wider for register form potentially */
}

h1 {
  color: var(--primary-color);
  margin-bottom: 2rem;
  text-align: center;
}
</style>
