<template>
    <header class="header">
      <nav class="main-nav">
        <router-link to="/" class="logo">{{ $t('navbar.logo') }}</router-link>
        <div class="nav-links">
          <router-link to="/">{{ $t('navbar.home') }}</router-link>
          <router-link to="/categories">{{ $t('navbar.categories') }}</router-link>
          <router-link to="/favorites">{{ $t('navbar.favorites') }}</router-link>
          <router-link to="/messages">{{ $t('navbar.messages') }}</router-link>
        </div>
        <div class="nav-right">
          <div class="language-switcher">
            <label for="language-select">{{ $t('navbar.language') }}: </label>
            <select id="language-select" v-model="locale">
              <option value="en">English</option>
              <option value="no">Norsk</option>
            </select>
          </div>
          <div class="auth-links">
            <LoggedInNavBar v-if="isLoggedIn" />
            <LoggedOutNavBar v-else />
          </div>
        </div>
      </nav>
    </header>
  </template>

  <script setup lang="ts">
  import { computed } from 'vue';
  import { useUserStore } from '@/stores/user';
  import LoggedInNavBar from './LoggedInNavBar.vue';
  import LoggedOutNavBar from './LoggedOutNavBar.vue';
  import { useI18n } from 'vue-i18n';

  const { locale } = useI18n();
  const userStore = useUserStore();
  const isLoggedIn = computed(() => userStore.isLoggedIn);

  
  </script>

  <style scoped>
  .header {
    background-color: var(--primary-color);
    padding: var(--spacing-unit);
    color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .main-nav {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .nav-links {
    display: flex;
    gap: calc(var(--spacing-unit) * 2);
  }

  .nav-links a,
  .auth-links a,
  .auth-links button,
  .language-switcher select,
  .language-switcher label {
    color: white;
    text-decoration: none;
    padding: calc(var(--spacing-unit) * 0.5) var(--spacing-unit);
    border-radius: var(--border-radius);
    transition: background-color 0.3s;
  }

  .language-switcher select {
      background-color: var(--primary-color);
      border: 1px solid white;
      padding: calc(var(--spacing-unit) * 0.5);
      cursor: pointer;
  }

  .language-switcher option {
      background-color: white;
      color: var(--primary-color);
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: calc(var(--spacing-unit) * 2);
  }

  .auth-links {
      display: flex;
      gap: var(--spacing-unit);
  }
  </style>