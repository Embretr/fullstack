<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useGetAllItems } from '../api/item-management/item-management';

const router = useRouter();

// Fetch all available items from the database
const { data: itemsData } = useGetAllItems();

// State for filters
const searchQuery = ref('');
const minPrice = ref<number | null>(null);
const maxPrice = ref<number | null>(null);
const location = ref('');
const showFilters = ref(false); // State to toggle filter visibility

// Computed filtered items
const filteredItems = computed(() => {
  if (!itemsData.value?.data) return [];
  return itemsData.value.data.filter((item) => {
    const matchesSearch = item.title.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesPrice =
      (minPrice.value === null || item.price >= minPrice.value) &&
      (maxPrice.value === null || item.price <= maxPrice.value);
    const matchesLocation = location.value === '' || item.location?.toLowerCase().includes(location.value.toLowerCase());
    return matchesSearch && matchesPrice && matchesLocation;
  });
});

// Handle price input
const handlePriceInput = (type: 'min' | 'max') => {
  if (type === 'min' && minPrice.value === '') {
    minPrice.value = null;
  }
  if (type === 'max' && maxPrice.value === '') {
    maxPrice.value = null;
  }
};

const validatePriceInput = (type: 'min' | 'max') => {
  if (type === 'min') {
    minPrice.value = minPrice.value?.replace(/[^0-9]/g, '') || null;
  }
  if (type === 'max') {
    maxPrice.value = maxPrice.value?.replace(/[^0-9]/g, '') || null;
  }
};
</script>

<template>
  <div class="home">
    <!-- Search Bar and Filters Button -->
    <div class="search-bar">
      <button class="filters-btn" @click="showFilters = !showFilters">
        {{ showFilters ? 'Hide Filters' : 'Filters' }}
      </button>
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search for items by name"
        class="search-input"
      />
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Filter Section -->
      <div class="filter-container" v-if="showFilters">
        <h3>Filters</h3>

<!-- Price Filter -->
<div class="filter-group">
  <label for="minPrice">Min Price:</label>
  <input
    type="text"
    id="minPrice"
    v-model="minPrice"
    @input="validatePriceInput('min')"
    placeholder="Enter minimum price"
  />
  <label for="maxPrice">Max Price:</label>
  <input
    type="text"
    id="maxPrice"
    v-model="maxPrice"
    @input="validatePriceInput('max')"
    placeholder="Enter maximum price"
  />
</div>

        <!-- Location Filter -->
        <div class="filter-group">
          <label for="location">Location:</label>
          <input
            type="text"
            id="location"
            v-model="location"
            placeholder="Enter location"
          />
        </div>
      </div>

      <!-- Items List -->
      <div class="content-container">
        <div class="items-list">
          <div
            v-for="item in filteredItems"
            :key="item.id"
            class="item-card"
            @click="router.push(`/item/${item.id}`)"
          >
            <img
              :src="item.imageUrls?.[0] || 'default-image-url.jpg'"
              alt="Item Image"
              class="item-image"
            />
            <h3>{{ item.title }}</h3>
            <p>{{ item.briefDescription || 'No description available' }}</p>
            <p>${{ item.price }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  text-align: center;
}

.search-input {
  flex: 1;
  max-width: 500px;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  font-size: 1rem;
}

.filters-btn {
  padding: 0.25rem 0.5rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 0.875rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.filters-btn:hover {
  background-color: #3aa876;
}

.main-content {
  display: flex;
  gap: 2rem;
}

.filter-container {
  width: 250px;
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-container h3 {
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.filter-group {
  margin-bottom: 1rem;
}

.filter-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.filter-group input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  font-size: 1rem;
}

.content-container {
  flex: 1;
}

.items-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.item-card {
  width: 200px;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  padding: 1rem;
  text-align: center;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
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