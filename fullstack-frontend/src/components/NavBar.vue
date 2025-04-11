<template>
  <header class="header">
    <nav class="main-nav">
      <!-- Add the logo here -->
      <router-link to="/" class="logo">
        <img src="@/assets/logo.png" alt="Logo" class="logo-image" />
      </router-link>
      <div class="nav-links" :class="{ 'hidden': isMobileMenuOpen }">
        <router-link to="/">{{ $t('navbar.home') }}</router-link>
        <router-link to="/categories">{{ $t('navbar.categories') }}</router-link>
        <router-link v-if="isLoggedIn" to="/favorites">{{ $t('navbar.favorites') }}</router-link>
        <router-link v-if="isLoggedIn" to="/messages">{{ $t('navbar.messages') }}</router-link>
      </div>
      <div class="nav-right">
        <div class="auth-links">
          <LoggedInNavBar v-if="isLoggedIn" />
          <LoggedOutNavBar v-else />
        </div>
      </div>
      <!-- Mobile menu toggle button -->
      <button class="menu-toggle" @click="toggleMobileMenu">
        ☰
      </button>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useAuthStore } from '@/stores/auth';
import LoggedInNavBar from './LoggedInNavBar.vue';
import LoggedOutNavBar from './LoggedOutNavBar.vue';


const authStore = useAuthStore();
const isLoggedIn = computed(() => authStore.isAuthenticated);

// State for mobile menu toggle
const isMobileMenuOpen = ref(false);
const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

// Reset menu state on window resize
const handleResize = () => {
  if (window.innerWidth > 768) {
    isMobileMenuOpen.value = false;
  }
};

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});
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
  flex-wrap: wrap;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-image {
  height: 50px;
  margin-right: 10px;
}

.nav-links {
  display: flex;
  gap: calc(var(--spacing-unit) * 2);
}

.nav-links.hidden {
  display: none;
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
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

.nav-links a:hover,
.auth-links a:hover,
.auth-links button:hover,
.language-switcher select:hover {
  background-color: rgba(255, 255, 255, 0.2); /* Slightly lighter background */
  color: white; /* Keep text color white */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Add a subtle shadow */
  cursor: pointer;
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

/* Mobile menu toggle button */
.menu-toggle {
  display: none;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.menu-toggle:hover {
  transform: scale(1.1); /* Slight zoom effect */
}

/* Responsive styles */
@media (max-width: 768px) {
  .nav-links {
    flex-direction: column;
    width: 100%;
    text-align: center;
  }

  .nav-right {
    flex-direction: column;
    gap: var(--spacing-unit);
  }

  .menu-toggle {
    display: block;
  }
}
</style>