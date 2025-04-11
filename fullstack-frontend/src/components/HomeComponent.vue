<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useGetAllItems } from '../api/item-management/item-management';
import Button from './common/Button.vue';
import Input from './common/Input.vue';
import FormGroup from './common/FormGroup.vue';
import ItemCard from './common/ItemCard.vue';

const router = useRouter();

// Fetch all available items from the database
const { data: itemsData } = useGetAllItems();

// State for filters
const searchQuery = ref('');
const minPrice = ref<string>('');
const maxPrice = ref<string>('');
const showFilters = ref(false); // State to toggle filter visibility

// Computed filtered items
const filteredItems = computed(() => {
  if (!itemsData.value?.data) return [];
  return itemsData.value.data.filter((item) => {
    const matchesSearch = item.title?.toLowerCase().includes(searchQuery.value.toLowerCase()) ?? false;
    const itemPrice = item.price ?? 0;
    const minPriceNum = minPrice.value ? Number(minPrice.value) : null;
    const maxPriceNum = maxPrice.value ? Number(maxPrice.value) : null;
    const matchesPrice =
      (minPriceNum === null || itemPrice >= minPriceNum) &&
      (maxPriceNum === null || itemPrice <= maxPriceNum);
    return matchesSearch && matchesPrice;
  }).map(item => ({
    ...item,
    title: item.title ?? '',
    briefDescription: item.briefDescription ?? '',
    price: item.price ?? 0,
    imageUrls: item.imageUrls ?? []
  }));
});

// Handle price input
const handlePriceInput = (type: 'min' | 'max') => {
  if (type === 'min' && minPrice.value === '') {
    minPrice.value = '';
  }
  if (type === 'max' && maxPrice.value === '') {
    maxPrice.value = '';
  }
};

const validatePriceInput = (type: 'min' | 'max') => {
  if (type === 'min') {
    minPrice.value = minPrice.value.replace(/[^0-9]/g, '');
  }
  if (type === 'max') {
    maxPrice.value = maxPrice.value.replace(/[^0-9]/g, '');
  }
};
</script>

<template>
  <div class="home">
    <!-- Search Bar and Filters Button -->
    <div class="search-bar">
      <Button
        variant="secondary"
        size="medium"
        @click="showFilters = !showFilters"
      >
        {{ showFilters ? 'Hide Filters' : 'Filters' }}
      </Button>
      <Input
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
        <FormGroup label="Price Range">
          <div class="price-filters">
            <Input
              v-model="minPrice"
              placeholder="Min price"
              @input="validatePriceInput('min')"
            />
            <Input
              v-model="maxPrice"
              placeholder="Max price"
              @input="validatePriceInput('max')"
            />
          </div>
        </FormGroup>
      </div>

      <!-- Items List -->
      <div class="content-container">
        <div class="items-list">
          <ItemCard
            v-for="item in filteredItems"
            :key="item.id"
            :item="item"
            @click="router.push(`/item/${item.id}`)"
          />
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
}

.search-input {
  flex: 1;
  max-width: 500px;
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

.price-filters {
  display: flex;
  gap: 0.5rem;
}

.content-container {
  flex: 1;
}

.items-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}
</style>