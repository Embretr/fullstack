<script setup lang="ts">
import { ref } from '@vue/reactivity';
import axios from 'axios';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const itemName = ref('');
const itemDescription = ref('');
const itemPrice = ref(0.0);
const itemCategory = ref('');
const itemImage = ref<File | null>(null);
const API_URL = 'http://localhost:8080/api';

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files?.[0]) {
    itemImage.value = target.files[0];
  }
};

const handleSubmit = async () => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    alert(t('createItemForm.errorNotLoggedIn'));
    window.location.href = '/login';
    return;
  }

  try {
    const formData = new FormData();
    formData.append('name', itemName.value);
    formData.append('description', itemDescription.value);
    formData.append('price', itemPrice.value.toString());
    formData.append('category', itemCategory.value);
    if (itemImage.value) {
      formData.append('image', itemImage.value);
    }

    const response = await axios.post(
      `${API_URL}/createItem`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      }
    );

    alert(t('createItemForm.successMessage'));
    console.log('Created item:', response.data);

    // Clear form fields
    itemName.value = '';
    itemDescription.value = '';
    itemPrice.value = 0;
    itemCategory.value = '';
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      console.error('Error creating item:', error.response?.data || error.message);
    } else {
      console.error('Error creating item:', error);
    }
    alert(t('createItemForm.errorDefault'));
  }
};
</script>

<template>
  <div class="create-item-form">
    <h2>{{ $t('createItemForm.title') }}</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">{{ $t('createItemForm.nameLabel') }}</label>
        <input type="text" id="name" v-model="itemName" required />
      </div>
      <div class="form-group">
        <label for="description">{{ $t('createItemForm.descriptionLabel') }}</label>
        <textarea id="description" v-model="itemDescription" required></textarea>
      </div>
      <div class="form-group">
        <label for="price">{{ $t('createItemForm.priceLabel') }}</label>
        <input type="number" step="0.01" id="price" v-model.number="itemPrice" required />
      </div>
      <div class="form-group">
        <label for="category">{{ $t('createItemForm.categoryLabel') }}</label>
        <select id="category" v-model="itemCategory" required>
          <option value="" disabled>{{ $t('createItemForm.categorySelectDefault') }}</option>
          <option value="electronics">{{ $t('createItemForm.categoryElectronics') }}</option>
          <option value="clothing">{{ $t('createItemForm.categoryClothing') }}</option>
          <option value="home">{{ $t('createItemForm.categoryHome') }}</option>
          <option value="toys">{{ $t('createItemForm.categoryToys') }}</option>
        </select>
      </div>
      <div class="form-group">
        <label for="image">Upload Image:</label>
        <input type="file" id="image" @change="handleFileChange" accept="image/*" />
      </div>
      <button class="submit-btn" type="submit">{{ $t('userProfile.createItemButton') }}</button>
    </form>
  </div>
</template>

<style scoped>
.create-item-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

input,
textarea,
select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  font-size: 1rem;
}

textarea {
  resize: vertical;
}

.submit-btn {
  display: block;
  width: 100%;
  padding: 0.75rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #3aa876;
}
</style>