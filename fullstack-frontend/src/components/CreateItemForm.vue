<script setup lang="ts">
import { ref } from '@vue/reactivity';
import { useI18n } from 'vue-i18n';
import { useCreateItem } from '../api/item-management/item-management';
import type { CreateItemParams } from '../api/model';

const { t } = useI18n();
const itemName = ref('');
const itemDescription = ref('');
const itemPrice = ref(0.0);
const itemCategory = ref('');
const itemImage = ref<File | null>(null);

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files?.[0]) {
    itemImage.value = target.files[0];
  }
};

const validatePriceInput = () => {
  // Convert to string for manipulation
  let priceStr = itemPrice.value.toString();
  
  // Remove any non-numeric characters except for the decimal point
  priceStr = priceStr.replace(/[^0-9.]/g, '');

  // Ensure only one decimal point is allowed
  if ((priceStr.match(/\./g) || []).length > 1) {
    priceStr = priceStr.slice(0, -1);
  }

  // Convert back to number
  itemPrice.value = Number.parseFloat(priceStr) || 0;
};

const { mutate: createItemMutation } = useCreateItem({
  mutation: {
    onSuccess: (data) => {
      alert(t('createItemForm.successMessage'));
      console.log('Created item:', data.data);
      // Clear form fields
      itemName.value = '';
      itemDescription.value = '';
      itemPrice.value = 0;
      itemCategory.value = '';
    },
    onError: (error) => {
      console.error('Error creating item:', error.response?.data || error.message);
      alert(t('createItemForm.errorDefault'));
    }
  }
});

const handleSubmit = async () => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    alert(t('createItemForm.errorNotLoggedIn'));
    window.location.href = '/login';
    return;
  }

  const formData = new FormData();
  formData.append('name', itemName.value);
  formData.append('description', itemDescription.value);
  formData.append('price', itemPrice.value.toString());
  formData.append('category', itemCategory.value);
  if (itemImage.value) {
    formData.append('image', itemImage.value);
  }

  const params: CreateItemParams = {
    images: itemImage.value ? [itemImage.value] : [],
    itemData: JSON.stringify({
      name: itemName.value,
      description: itemDescription.value,
      price: itemPrice.value,
      category: itemCategory.value
    })
  };

  createItemMutation({ params });
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
        <input
          type="text"
          id="price"
          v-model="itemPrice"
          @input="validatePriceInput"
          placeholder="Enter price"
          required
        />
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