<template>
  <nav class="navbar">
        <!-- List Item Button -->
        <router-link to="/listItem" class="list-item-button">
      {{ $t('navbar.listItem') }}
    </router-link>
    <!-- Dropdown Menu -->
    <div class="dropdown">
      <button class="dropdown-button">{{ $t('navbar.menu') }}</button>
      <div class="dropdown-content">
        <router-link to="/admin">{{ $t('navbar.adminPanel') }}</router-link>
        <router-link to="/profile">{{ $t('navbar.myProfile') }}</router-link>
        <router-link to="/settings">{{ $t('navbar.settings') }}</router-link>
        <button @click="handleLogout">{{ $t('navbar.logout') }}</button>
      </div>
    </div>
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
.navbar {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-button,
.list-item-button {
  padding: 0.5rem 1.5rem; /* Ensure consistent padding for all buttons */
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none; /* Remove underline from text */
  text-align: center;
  font-size: 1rem; /* Ensure consistent font size */
  display: inline-block; /* Ensure consistent display */
  transition: background-color 0.3s ease;
}

.dropdown-button:hover,
.list-item-button:hover {
  background-color: #0056b3;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  min-width: 150px;
  z-index: 1;
}

.dropdown-content a,
.dropdown-content button {
  display: block;
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: black;
  background: none;
  border: none;
  text-align: left;
  width: 100%;
  cursor: pointer;
}

.dropdown-content a:hover,
.dropdown-content button:hover {
  background-color: #f1f1f1;
}

.dropdown:hover .dropdown-content {
  display: block;
}
</style>