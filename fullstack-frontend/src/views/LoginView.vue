<template>
  <div class="login">
    <div class="login-container">
      <h1>{{ $t('navbar.login') }}</h1>
      <LoginComponent />
      <p class="register-link">
        {{ $t('loginView.dontHaveAccount') }}<router-link to="/register">{{ $t('navbar.register') }}</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginComponent from '@/components/LoginComponent.vue'
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 2rem;
}

.login-container {
  background: white;
  padding: 2rem;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  color: var(--primary-color);
  margin-bottom: 2rem;
  text-align: center;
}

.register-link {
  text-align: center;
  margin-top: 1.5rem;
  color: #666;
}

.register-link a {
  color: var(--secondary-color);
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>