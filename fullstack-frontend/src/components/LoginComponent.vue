<template>
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label for="email">{{ $t('loginForm.emailLabel') }}</label>
        <input
          type="email"
          id="email"
          v-model="email"
          required
          :placeholder="$t('loginForm.emailPlaceholder')"
        />
      </div>
      <div class="form-group">
        <label for="password">{{ $t('loginForm.passwordLabel') }}</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          :placeholder="$t('loginForm.passwordPlaceholder')"
        />
      </div>
      <button type="submit" class="submit-btn">{{ $t('loginForm.loginButton') }}</button>
    </form>
  </template>

  <script setup lang="ts">
  import { ref } from 'vue';
  import { useAuthStore } from '@/stores/auth';
  import { useI18n } from 'vue-i18n';
  import { useRouter } from 'vue-router';

  const { t } = useI18n();
  const router = useRouter();
  const email = ref('');
  const password = ref('');
  const username = ref('');
  const authStore = useAuthStore();

  const handleLogin = async () => {
    try {
      await authStore.login({
        email: email.value,
        password: password.value,
        username: username.value
      });
      router.push('/');
    } catch (error) {
      if (error instanceof Error) {
        alert(error.message);
      } else {
        alert(t('loginForm.errorUnexpected'));
      }
    }
  };
  </script>

  <style scoped>
  .login-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  label {
    color: var(--primary-color);
    font-weight: bold;
  }

  input {
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: var(--border-radius);
    font-size: 1rem;
  }

  .submit-btn {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .submit-btn:hover {
    background-color: #0056b3;
  }
  </style>