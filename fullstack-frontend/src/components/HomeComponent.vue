<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useGetAllItems } from '../api/item-management/item-management';



const router = useRouter();

// Fetch all available items from the database
const { data: itemsData } = useGetAllItems();

// Redirect logic
const handleRedirect = (): void => {
  const token = localStorage.getItem('authToken'); // Check if the user is logged in
  if (token) {
    router.push('/createItem'); // Redirect to Create Item page
  } else {
    router.push('/login'); // Redirect to Login page
  }
};

</script>

<template>
  <div class="home">
    <!-- Redirect Button -->
    <button class="redirect-btn" @click="handleRedirect">{{ $t('userProfile.createItemButton') }}</button>

    <!-- Items List -->
    <div class="items-list">
      <div v-for="item in itemsData?.data" :key="item.id" class="item-card">
        <img :src="item.images?.[0]?.imageUrl || 'default-image-url.jpg'" alt="Item Image" class="item-image" />
        <h3>{{ item.title }}</h3>
        <p>{{ item.briefDescription || 'No description available' }}</p>
        <p>${{ item.price }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  position: relative;
  padding: 2rem;
}

.redirect-btn {
  position: absolute;
  top: 1rem;
  left: 1rem;
  padding: 0.5rem 1rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.redirect-btn:hover {
  background-color: #3aa876;
}

.items-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 2rem;
}

.item-card {
  width: 200px;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  padding: 1rem;
  text-align: center;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.item-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: var(--border-radius);
  margin-bottom: 0.5rem;
}

.item-card h3 {
  font-size: 1.2rem;
  margin: 0;
}

.item-card p {
  margin: 0.5rem 0;
}
</style>