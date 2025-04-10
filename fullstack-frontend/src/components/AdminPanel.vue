<script setup lang="ts">
import { ref, computed } from 'vue';
import { useGetAllItems } from '../api/item-management/item-management';
import { useDeleteItem } from '../api/item-management/item-management';
import { useGetAllCategories } from '../api/category-management/category-management';
import { useCreateCategory } from '../api/category-management/category-management';
import { useGetUserByEmail } from '../api/user-management/user-management';
import { useMakeUserAdmin } from '../api/user-management/user-management';
import { useRemoveAdminRole } from '../api/user-management/user-management';
import { useDeleteUserByEmail } from '../api/user-management/user-management';
import { useGetAllUsers } from '../api/user-management/user-management'; // Add API for fetching all users
import type {  Category } from '../api/model';
import type { AxiosError } from 'axios';

// Search and filter state
const searchQuery = ref('');
const selectedCategory = ref<number | null>(null);
const showItems = ref(true);
const userEmail = ref('');

// Fetch all items
const { data: items, isLoading: itemsLoading, error: itemsError } = useGetAllItems();

// Delete an item
const { mutate: deleteItemMutation } = useDeleteItem({
  mutation: {
    onSuccess: (_, ) => {
      // Vue Query will automatically refetch the items list
    },
    onError: (error: AxiosError) => {
      console.error('Error deleting item:', error);
      alert('Failed to delete item. Please try again.');
    }
  }
});

const deleteItem = async (itemId: number): Promise<void> => {
  if (!confirm('Are you sure you want to delete this item?')) return;
  deleteItemMutation({ itemId });
};

// Fetch all categories
const { data: categories } = useGetAllCategories();

// Create a new category
const { mutate: createCategoryMutation } = useCreateCategory({
  mutation: {
    onSuccess: () => {
      newCategoryName.value = '';
      // Vue Query will automatically refetch the categories list
    },
    onError: (error: AxiosError) => {
      console.error('Error creating category:', error);
      alert('Failed to create category. Please try again.');
    }
  }
});

const newCategoryName = ref('');

const createCategory = async (): Promise<void> => {
  if (!newCategoryName.value.trim()) return;

  const category: Category = {
    name: newCategoryName.value.trim()
  };

  createCategoryMutation({ data: category });
};

// Fetch user details
const { data: userDetails, isLoading: userLoading, error: userError } = useGetUserByEmail(userEmail, {
  query: {
    enabled: computed(() => !!userEmail.value)
  }
});

// Make user admin
const { mutate: makeUserAdminMutation } = useMakeUserAdmin({
  mutation: {
    onSuccess: () => {
      // Vue Query will automatically refetch the user details
    },
    onError: (error: AxiosError) => {
      console.error('Error making user admin:', error);
      alert('Failed to make user admin. Please try again.');
    }
  }
});

const makeUserAdmin = async (): Promise<void> => {
  if (!userEmail.value) return;
  makeUserAdminMutation({ email: userEmail.value });
};

// Remove admin role
const { mutate: removeAdminRoleMutation } = useRemoveAdminRole({
  mutation: {
    onSuccess: () => {
      // Vue Query will automatically refetch the user details
    },
    onError: (error: AxiosError) => {
      console.error('Error removing admin role:', error);
      alert('Failed to remove admin role. Please try again.');
    }
  }
});

const removeAdminRole = async (): Promise<void> => {
  if (!userEmail.value) return;
  removeAdminRoleMutation({ email: userEmail.value });
};

// Delete user
const { mutate: deleteUserMutation } = useDeleteUserByEmail({
  mutation: {
    onSuccess: () => {
      userEmail.value = '';
    },
    onError: (error: AxiosError) => {
      console.error('Error deleting user:', error);
      alert('Failed to delete user. Please try again.');
    }
  }
});

const deleteUser = async (): Promise<void> => {
  if (!userEmail.value) return;
  if (!confirm('Are you sure you want to delete this user?')) return;
  deleteUserMutation({ email: userEmail.value });
};

// Fetch all users
const { data: users, isLoading: usersLoading, error: usersError } = useGetAllUsers();

// Toggle users visibility
const showUsers = ref(false); // Controls whether users are displayed
const toggleUsers = () => {
  showUsers.value = !showUsers.value;
};

// Computed property to filter items
const filteredItems = computed(() => {
  if (!showItems.value || !items.value?.data) return [];

  let result = [...items.value.data];

  // Filter by search query
  if (searchQuery.value) {
    result = result.filter((item) =>
      item.title?.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // Filter by category
  if (selectedCategory.value) {
    result = result.filter((item) => item.category?.id === selectedCategory.value);
  }

  return result;
});
</script>

<template>
  <div class="admin-panel">
    <h2>Admin Panel</h2>

    <!-- Error Message -->
    <p v-if="itemsError" class="error-message">{{ itemsError.message }}</p>

    <!-- Loading State -->
    <div v-if="itemsLoading" class="loading-state">
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
            <option v-for="category in categories?.data" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>
      </div>

      <div v-if="!itemsLoading">
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
              <td>{{ item.briefDescription || 'No description' }}</td>
              <td>${{ item.price?.toFixed(2) }}</td>
              <td>
                {{ item.category?.name || 'Uncategorized' }}
              </td>
              <td>
                <img v-if="item.imageUrls?.[0]" :src="item.imageUrls[0]" alt="Item Image" class="item-image" />
                <span v-else>No image</span>
              </td>
              <td>
                <button @click="deleteItem(item.id!)" class="delete-btn">Delete</button>
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

      <table v-if="categories?.data?.length" class="categories-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories.data" :key="category.id">
            <td>{{ category.id }}</td>
            <td>{{ category.name }}</td>
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
        <button class="lookup-user-btn" :disabled="userLoading">
          {{ userLoading ? 'Searching...' : 'Lookup User' }}
        </button>
      </div>

      <p v-if="userError" class="user-error">{{ userError.message }}</p>

      <div v-if="userDetails?.data" class="user-details">
        <h4>User Details</h4>
        <div class="detail-row">
          <span class="detail-label">Email:</span>
          <span>{{ userDetails.data.email }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">Name:</span>
          <span>{{ userDetails.data.username }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">Role:</span>
          <span class="role-badge" :class="{ 'admin-role': userDetails.data.role === 'ADMIN' }">
            {{ userDetails.data.role }}
          </span>
        </div>

        <button
          v-if="userDetails.data.role !== 'ADMIN'"
          @click="makeUserAdmin"
          class="make-admin-btn"
        >
          Make Admin
        </button>

        <button
          v-if="userDetails.data.role === 'ADMIN'"
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

      <button @click="toggleUsers" class="toggle-users-btn">
        {{ showUsers ? 'Hide Users' : 'Show Users' }}
      </button>

      <div v-if="usersLoading" class="loading-state">
        <p>Loading users...</p>
      </div>

      <p v-if="usersError" class="user-error">{{ usersError.message }}</p>

      <table v-if="showUsers && users?.data?.length" class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Username</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users.data" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.role }}</td>
          </tr>
        </tbody>
      </table>

      <p v-else-if="showUsers && !users?.data?.length" class="no-users-message">
        No users found.
      </p>
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

.items-table, .categories-table, .users-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

.items-table th, .items-table td,
.categories-table th, .categories-table td,
.users-table th, .users-table td {
  border: 1px solid #ddd;
  padding: 0.75rem;
  text-align: left;
}

.items-table th, .categories-table th, .users-table th {
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

.no-items-message, .no-categories-message, .no-users-message {
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

.toggle-users-btn {
  padding: 0.5rem 1rem;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.toggle-users-btn:hover {
  background-color: #0b7dda;
}
</style>