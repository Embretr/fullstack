<script setup lang="ts">
import { ref, computed } from '@vue/reactivity';
import { onMounted, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useCreateItem } from '../api/item-management/item-management';
import { useGetAllCategories } from '../api/category-management/category-management';
import type { CreateItemParams, CategoryResponseDTO } from '../api/model';
import axios from 'axios';

const { t } = useI18n();
const itemName = ref('');
const itemDescription = ref('');
const itemPrice = ref(0.0);
const itemCategory = ref<number | ''>('');
const itemImage = ref<File | null>(null);
const itemImages = ref<File[]>([]);
const imagePreviews = ref<string[]>([]);
const MAX_FILE_SIZE = 512 * 1024 * 1024; // 512MB in bytes

// Fetch categories
const { data: categoriesData } = useGetAllCategories();
const categories = computed(() => categoriesData.value?.data ?? []);

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    const files = Array.from(target.files);
    console.log('Selected files:', files);
    
    // Check file sizes
    for (const file of files) {
      if (file.size > MAX_FILE_SIZE) {
        console.error('File too large:', file.name, file.size);
        alert(t('createItemForm.errorFileTooLarge'));
        target.value = ''; // Clear the input
        itemImages.value = [];
        imagePreviews.value = [];
        return;
      }
    }
    
    itemImages.value = files;
    console.log('Stored files in itemImages:', itemImages.value);
    
    // Create preview URLs
    imagePreviews.value = [];
    for (const file of itemImages.value) {
      const previewUrl = URL.createObjectURL(file);
      console.log('Created preview URL for:', file.name, previewUrl);
      imagePreviews.value.push(previewUrl);
    }
  }
};

// Clean up preview URLs when component is unmounted
onUnmounted(() => {
  for (const url of imagePreviews.value) {
    URL.revokeObjectURL(url);
  }
});

const removeImage = (index: number) => {
  itemImages.value.splice(index, 1);
  URL.revokeObjectURL(imagePreviews.value[index]);
  imagePreviews.value.splice(index, 1);
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
  console.log('Starting form submission');
  console.log('Form data:', {
    title: itemName.value,
    description: itemDescription.value,
    price: itemPrice.value,
    categoryId: itemCategory.value,
    images: itemImages.value
  });

  // Create item data
  const itemData = {
    title: itemName.value,
    briefDescription: itemDescription.value,
    fullDescription: itemDescription.value,
    price: itemPrice.value,
    categoryId: Number(itemCategory.value),
    latitude: 0,
    longitude: 0
  };
  console.log('Item data to be sent:', itemData);

  // Create params for the API client
  const params: CreateItemParams = {
    images: itemImages.value,
    itemData: JSON.stringify(itemData)
  };
  console.log('API params:', {
    images: params.images,
    itemData: params.itemData
  });

  // Call the mutation
  console.log('Calling createItemMutation');
  createItemMutation({ 
    params,
    mutation: {
      onSuccess: (data) => {
        console.log('Item created successfully:', data);
        alert(t('createItemForm.successMessage'));
        // Clear form fields
        itemName.value = '';
        itemDescription.value = '';
        itemPrice.value = 0;
        itemCategory.value = '';
        itemImages.value = [];
        imagePreviews.value = [];
      },
      onError: (error) => {
        console.error('Error creating item:', error);
        console.error('Error response:', error.response?.data);
        alert(t('createItemForm.errorDefault'));
      }
    }
  });
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
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label for="images">Upload Images:</label>
        <input 
          type="file" 
          id="images" 
          @change="handleFileChange" 
          accept="image/*" 
          multiple
        />
        <div class="image-previews" v-if="imagePreviews.length > 0">
          <div v-for="(preview, index) in imagePreviews" :key="index" class="image-preview">
            <img :src="preview" :alt="'Preview ' + (index + 1)" />
            <button type="button" @click="removeImage(index)" class="remove-image">×</button>
          </div>
        </div>
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

.image-previews {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 1rem;
}

.image-preview {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: var(--border-radius);
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.remove-image:hover {
  background-color: rgba(255, 255, 255, 1);
}
</style>