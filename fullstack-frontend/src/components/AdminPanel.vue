<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

interface Item {
  id: number;
  title: string;
  description: string;
  price: number;
  categoryId?: number;
  imageUrl?: string;
}

interface Category {
  id: number;
  name: string;
}

interface User {
  id: number;
  email: string;
  username: string;
  role: string;
}

// Items and Categories
const items = ref<Item[]>([]);
const categories = ref<Category[]>([]);
const newCategoryName = ref('');
const searchQuery = ref('');
const selectedCategory = ref<number | null>(null);
const showItems = ref(true);
const isLoading = ref(false);
const errorMessage = ref('');

// User management
const userEmail = ref('');
const userDetails = ref<User | null>(null);
const userError = ref('');
const isUserLoading = ref(false);

// Computed property to filter items
const filteredItems = computed(() => {
  if (!showItems.value || !items.value) return [];

  let result = [...items.value];

  // Filter by search query
  if (searchQuery.value) {
    result = result.filter((item) =>
      item.title?.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // Filter by category
  if (selectedCategory.value) {
    result = result.filter((item) => item.categoryId === selectedCategory.value);
  }

  return result;
});

// Fetch all items
const fetchItems = async (): Promise<void> => {
  isLoading.value = true;
  try {
    const response = await axios.get<Item[]>('http://localhost:8080/api/marketplace/items');
    items.value = response.data || [];
  } catch (error) {
    console.error('Error fetching items:', error);
    errorMessage.value = 'Failed to load items. Please try again later.';
    items.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Delete an item
const deleteItem = async (itemId: number): Promise<void> => {
  if (!confirm('Are you sure you want to delete this item?')) return;

  try {
    const token = localStorage.getItem('authToken');
    await axios.delete(`http://localhost:8080/api/marketplace/items/${itemId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    items.value = items.value.filter(item => item.id !== itemId);
  } catch (error) {
    console.error('Error deleting item:', error);
    alert('Failed to delete item. Please try again.');
  }
};

// Fetch all categories
const fetchCategories = async (): Promise<void> => {
  try {
    const response = await axios.get<Category[]>('http://localhost:8080/api/marketplace/categories');
    categories.value = response.data || [];
  } catch (error) {
    console.error('Error fetching categories:', error);
    alert('Failed to load categories. Please try again later.');
    categories.value = [];
  }
};

// Create a new category
const createCategory = async (): Promise<void> => {
  if (!newCategoryName.value.trim()) {
    alert('Category name cannot be empty.');
    return;
  }

  try {
    const token = localStorage.getItem('authToken');
    const response = await axios.put<Category>('http://localhost:8080/api/marketplace/categories', {
      name: newCategoryName.value,
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    categories.value.push(response.data);
    newCategoryName.value = '';
    alert('Category created successfully.');
  } catch (error) {
    console.error('Error creating category:', error);
    alert('Failed to create category. Please try again.');
  }
};

// Delete a category
const deleteCategory = async (categoryId: number): Promise<void> => {
  if (!confirm('Are you sure you want to delete this category? Items in this category will become uncategorized.')) return;

  try {
    const token = localStorage.getItem('authToken');
    await axios.delete(`http://localhost:8080/api/marketplace/categories/${categoryId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    categories.value = categories.value.filter(c => c.id !== categoryId);
    // Update items that might have been in this category
    items.value = items.value.map(item => {
      if (item.categoryId === categoryId) {
        return { ...item, categoryId: undefined };
      }
      return item;
    });
  } catch (error) {
    console.error('Error deleting category:', error);
    alert('Failed to delete category. Please try again.');
  }
};

// User lookup
const lookupUser = async (): Promise<void> => {
  if (!userEmail.value.trim()) {
    userError.value = 'Email cannot be empty.';
    return;
  }

  isUserLoading.value = true;
  userError.value = '';

  try {
    const token = localStorage.getItem('authToken');
    const response = await axios.get<User>(`http://localhost:8080/api/marketplace/users/${userEmail.value}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    userDetails.value = response.data;
  } catch (error) {
    console.error('Error looking up user:', error);
    userDetails.value = null;

    if (axios.isAxiosError(error)) {
      if (error.response?.status === 404) {
        userError.value = 'User not found. Please check the email address.';
      } else if (error.response?.status === 401) {
        userError.value = 'Unauthorized. Please login again.';
      } else {
        userError.value = 'An error occurred while looking up the user.';
      }
    } else {
      userError.value = 'An unexpected error occurred.';
    }
  } finally {
    isUserLoading.value = false;
  }
};

// Make user admin
const makeUserAdmin = async (): Promise<void> => {
  if (!userDetails.value) {
    alert('No user selected.');
    return;
  }

  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('No authentication token found.');
      return;
    }

    const email = userDetails.value.email.toLowerCase().trim(); // Normalize email
    console.log('Making user admin for email:', email);

    await axios.put(
      `http://localhost:8080/api/marketplace/admin/${encodeURIComponent(email)}`,
      null,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    alert('User role updated to ADMIN successfully.');
    userDetails.value.role = 'ADMIN';
  } catch (error) {
    console.error('Error making user admin:', error);
    if (axios.isAxiosError(error)) {
      alert(`Failed to update user role: ${error.response?.data || error.message}`);
    } else {
      alert('Failed to update user role. Please try again.');
    }
  }
};

// Remove admin role from a user
const removeAdminRole = async (): Promise<void> => {
  if (!userDetails.value) {
    alert('No user selected.');
    return;
  }

  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('No authentication token found.');
      return;
    }

    const email = userDetails.value.email.toLowerCase().trim(); // Normalize email
    console.log('Removing admin role for email:', email);

    await axios.put(
      `http://localhost:8080/api/marketplace/admin/remove/${encodeURIComponent(email)}`,
      null,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    alert('Admin role removed successfully.');
    userDetails.value.role = 'USER'; // Update role locally
  } catch (error) {
    console.error('Error removing admin role:', error);
    if (axios.isAxiosError(error)) {
      alert(`Failed to remove admin role: ${error.response?.data || error.message}`);
    } else {
      alert('Failed to remove admin role. Please try again.');
    }
  }
};

// Delete a user
const deleteUser = async (): Promise<void> => {
  if (!userDetails.value) {
    alert('No user selected.');
    return;
  }

  if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) return;

  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('No authentication token found.');
      return;
    }

    const email = userDetails.value.email.toLowerCase().trim(); // Normalize email
    console.log('Deleting user with email:', email);

    await axios.delete(
      `http://localhost:8080/api/marketplace/users/${encodeURIComponent(email)}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    alert('User deleted successfully.');
    userDetails.value = null; // Clear user details
  } catch (error) {
    console.error('Error deleting user:', error);
    if (axios.isAxiosError(error)) {
      alert(`Failed to delete user: ${error.response?.data || error.message}`);
    } else {
      alert('Failed to delete user. Please try again.');
    }
  }
};

// Initialize data
onMounted(() => {
  fetchItems();
  fetchCategories();
});
</script>

<template>
  <div class="admin-panel">
    <h2>Admin Panel</h2>

    <!-- Error Message -->
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      <p>Loading data...</p>
    </div>

    <!-- Items Section -->
    <section class="section">
      <h3>Item Management</h3>

      <div class="controls">
        <div class="search-box">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search items by name"
            class="search-input"
          />
        </div>

        <div class="filter-controls">
          <button @click="showItems = !showItems" class="toggle-items-btn">
            {{ showItems ? 'Hide Items' : 'Show Items' }}
          </button>

          <select v-model="selectedCategory" class="category-filter">
            <option :value="null">All Categories</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>
      </div>

      <div v-if="!isLoading">
        <table v-if="showItems && filteredItems.length > 0" class="items-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Description</th>
              <th>Price</th>
              <th>Category</th>
              <th>Image</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredItems" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.title }}</td>
              <td>{{ item.description || 'No description' }}</td>
              <td>${{ item.price.toFixed(2) }}</td>
              <td>
                {{ categories.find(c => c.id === item.categoryId)?.name || 'Uncategorized' }}
              </td>
              <td>
                <img v-if="item.imageUrl" :src="item.imageUrl" alt="Item Image" class="item-image" />
                <span v-else>No image</span>
              </td>
              <td>
                <button @click="deleteItem(item.id)" class="delete-btn">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>

        <p v-else-if="showItems && filteredItems.length === 0" class="no-items-message">
          No items match your criteria.
        </p>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="section">
      <h3>Category Management</h3>

      <div class="category-controls">
        <input
          type="text"
          v-model="newCategoryName"
          placeholder="New category name"
          class="category-input"
        />
        <button @click="createCategory" class="create-category-btn">Create Category</button>
      </div>

      <table v-if="categories.length > 0" class="categories-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories" :key="category.id">
            <td>{{ category.id }}</td>
            <td>{{ category.name }}</td>
            <td>
              <button @click="deleteCategory(category.id)" class="delete-btn">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-else class="no-categories-message">No categories found.</p>
    </section>

    <!-- User Management Section -->
    <section class="section">
      <h3>User Management</h3>

      <div class="user-lookup">
        <input
          type="email"
          v-model="userEmail"
          placeholder="Enter user email"
          class="user-email-input"
        />
        <button @click="lookupUser" class="lookup-user-btn" :disabled="isUserLoading">
          {{ isUserLoading ? 'Searching...' : 'Lookup User' }}
        </button>
      </div>

      <p v-if="userError" class="user-error">{{ userError }}</p>

      <div v-if="userDetails" class="user-details">
  <h4>User Details</h4>
  <div class="detail-row">
    <span class="detail-label">Email:</span>
    <span>{{ userDetails.email }}</span>
  </div>
  <div class="detail-row">
    <span class="detail-label">Name:</span>
    <span>{{ userDetails.username }}</span>
  </div>
  <div class="detail-row">
    <span class="detail-label">Role:</span>
    <span class="role-badge" :class="{ 'admin-role': userDetails.role === 'ADMIN' }">
      {{ userDetails.role }}
    </span>
  </div>


  <button
    v-if="userDetails.role !== 'ADMIN'"
    @click="makeUserAdmin"
    class="make-admin-btn"
  >
    Make Admin
  </button>

  <button
    v-if="userDetails.role === 'ADMIN'"
    @click="removeAdminRole"
    class="remove-admin-btn"
  >
    Remove Admin Role
  </button>

  <button
    @click="deleteUser"
    class="delete-user-btn"
  >
    Delete User
  </button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.admin-panel {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.section {
  margin-bottom: 2rem;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #f9f9f9;
}

h2, h3, h4 {
  color: #333;
}

h2 {
  margin-bottom: 1.5rem;
  text-align: center;
}

h3 {
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #ddd;
}

.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
  align-items: center;
}

.search-box {
  flex: 1;
  min-width: 300px;
}

.search-input, .category-input, .user-email-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.category-filter {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 200px;
  font-size: 1rem;
}

.toggle-items-btn, .create-category-btn, .lookup-user-btn, .make-admin-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.toggle-items-btn {
  background-color: #4CAF50;
  color: white;
}

.toggle-items-btn:hover {
  background-color: #45a049;
}

.create-category-btn {
  background-color: #2196F3;
  color: white;
}

.create-category-btn:hover {
  background-color: #0b7dda;
}

.lookup-user-btn {
  background-color: #673AB7;
  color: white;
}

.lookup-user-btn:hover {
  background-color: #5e35b1;
}

.lookup-user-btn:disabled {
  background-color: #b0a8c5;
  cursor: not-allowed;
}

.make-admin-btn {
  background-color: #FF9800;
  color: white;
  margin-top: 1rem;
}

.make-admin-btn:hover {
  background-color: #e68a00;
}

.make-admin-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.remove-admin-btn {
  background-color: #ff9800;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 1rem;
}

.remove-admin-btn:hover {
  background-color: #e68a00;
}

.delete-user-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 1rem;
}

.delete-user-btn:hover {
  background-color: #d32f2f;
}

.category-controls {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.user-lookup {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.items-table, .categories-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

.items-table th, .items-table td,
.categories-table th, .categories-table td {
  border: 1px solid #ddd;
  padding: 0.75rem;
  text-align: left;
}

.items-table th, .categories-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.item-image {
  max-width: 100px;
  max-height: 100px;
  display: block;
}

.delete-btn {
  padding: 0.5rem 1rem;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

.loading-state {
  padding: 2rem;
  text-align: center;
  color: #666;
}

.no-items-message, .no-categories-message {
  padding: 1rem;
  text-align: center;
  color: #666;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.user-details {
  margin-top: 1rem;
  padding: 1rem;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.detail-row {
  display: flex;
  margin-bottom: 0.5rem;
}

.detail-label {
  font-weight: bold;
  min-width: 100px;
}

.role-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  background-color: #e0e0e0;
  color: #333;
}

.role-badge.admin-role {
  background-color: #4CAF50;
  color: white;
}

.user-error {
  color: #f44336;
  font-weight: bold;
  margin-top: 0.5rem;
}

.error-message {
  color: #f44336;
  font-weight: bold;
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #ffebee;
  border-radius: 4px;
}
</style>