<template>
    <nav class="navbar">
      <router-link class="button" to="/login">{{ $t('navbar.login') }}</router-link>
      <router-link class="button" to="/register">{{ $t('navbar.register') }}</router-link>
    </nav>
  </template>

  <script setup lang="ts">
  // No script logic needed for translations here, but could import useI18n if required later
  </script>

  <style scoped>
  .navbar {
    display: flex;
    gap: 1rem;
  }

  .button {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 4px;
    transition: background-color 0.3s ease;
  }
  </style>