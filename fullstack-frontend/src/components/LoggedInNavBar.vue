<template>
  <nav class="navbar">
    <!-- Dropdown Menu -->
    <div class="dropdown">
      <Button class="dropdown-button" variant="primary" size="medium">
        {{ $t('navbar.menu') }}
      </Button>
      <div class="dropdown-content">
        <router-link to="/admin">{{ $t('navbar.adminPanel') }}</router-link>
        <router-link to="/profile">{{ $t('navbar.myProfile') }}</router-link>
        <router-link to="/settings">{{ $t('navbar.settings') }}</router-link>
        <router-link to="/listItem">{{ $t('navbar.listItem') }}</router-link>
        <a href="#" @click.prevent="handleLogout">{{ $t('navbar.logout') }}</a>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import Button from './common/Button.vue';

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

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  min-width: 150px;
  z-index: 1;
  right: 0;
}

.dropdown-content a {
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

.dropdown-content a:hover {
  background-color: #f1f1f1;
}

.dropdown:hover .dropdown-content {
  display: block;
}


</style>