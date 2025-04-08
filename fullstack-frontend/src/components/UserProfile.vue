<template>
  <div class="user-profile">
    <h1>{{ $t('userProfile.welcome', { userName: userData?.data?.username || '' }) }}</h1>
    <p>{{ $t('userProfile.emailPrefix') }}{{ userData?.data?.email || '' }}</p>
    <button @click="logout" class="button">{{ $t('navbar.logout') }}</button>
    <button @click="goToSettings" class="button">{{ $t('userProfile.settingsButton') }}</button>
    <button @click="createItem" class="button">{{ $t('userProfile.createItemButton') }}</button>
  </div>
  <h1>My Items</h1>
  <div v-if="!userItems || userItems.length === 0">
    <p>You have not listed any items.</p>
    <button @click="createItem" class="button">List Item</button>
  </div>
  <div v-else class="items-list">
    <div v-for="item in userItems" :key="item.id" class="item">
      <img :src="getItemImageUrl(item)" :alt="item.title || 'Item Image'" />
      <h3>{{ item.title }}</h3>
      <p>{{ item.briefDescription || 'No description available' }}</p>
      <p>${{ item.price }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useGetUserItems } from '../api/item-management/item-management';
import { useGetUserByEmail } from '../api/user-management/user-management';
import type { Item, User } from '../api/model';
import type { UseQueryReturnType } from '@tanstack/vue-query';
import type { AxiosResponse, AxiosError } from 'axios';

const { t } = useI18n();
const router = useRouter();

// Get user data
const { data: userData } = useGetUserByEmail('', {
  query: {
    queryKey: ['user'],
    enabled: false,
    staleTime: 10000, // Cache for 10 seconds
  },
});

// Get user items
const { data: itemsData } = useGetUserItems({
  query: {
    queryKey: ['userItems'],
    staleTime: 10000, // Cache for 10 seconds
  },
});

// Compute user items with proper typing
const userItems = computed<Item[]>(() => {
  if (!itemsData.value?.data) return [];
  return (itemsData.value.data as unknown as Item[]) || [];
});

// Helper function to get item image URL
const getItemImageUrl = (item: Item): string => {
  if (!item.images?.length) return 'default-image-url.jpg';
  const image = item.images[0];
  return image ? `/api/items/images/${image}` : 'default-image-url.jpg';
};

const logout = async () => {
  try {
    localStorage.removeItem('authToken');
    router.push('/login');
  } catch (error) {
    if (error instanceof Error) {
      console.error('Logout failed:', error.message);
    }
    alert(t('logout.error'));
  }
};

const goToSettings = () => {
  router.push('/userSettings');
};

const createItem = () => {
  router.push('/createItem');
};
</script>

<style scoped>
.user-profile {
  text-align: center;
  margin-top: 2rem;
}

.button {
  background-color: var(--secondary-color);
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  margin: 0.5rem;
}

.button:hover {
  background-color: var(--secondary-color-hover);
}

.items-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
  padding: 1rem;
}

.item {
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: 1rem;
  text-align: center;
}

.item img {
  max-width: 100%;
  height: auto;
  border-radius: var(--border-radius);
  margin-bottom: 1rem;
}
</style>