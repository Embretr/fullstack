<template>
    <nav class="navbar">
      <router-link to="/admin">Admin Panel</router-link>
      <router-link class="button" to="/profile">{{ $t('navbar.myProfile') }}</router-link>
      <button class="button" @click="handleLogout">{{ $t('navbar.logout') }}</button>
    </nav>
  </template>

  <script setup lang="ts">
  import { useAuthStore } from '@/stores/auth';
  import { useI18n } from 'vue-i18n';
  import { useRouter } from 'vue-router';

  const { t } = useI18n();
  const router = useRouter();
  const authStore = useAuthStore();

  const handleLogout = async () => {
    try {
      await authStore.logout();
      router.push('/login');
    } catch (error) {
      console.error('Logout failed:', error);
      alert(t('logout.error'));
    }
  };
  </script>

  <style scoped>

  .button {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 4px;
    transition: background-color 0.3s ease;
    border: none;
    cursor: pointer;
  }

  .button:hover {
    background-color: #0056b3;
  }

  .navbar {
    display: flex;
    gap: 1rem;
    align-items: center;
  }
  </style>