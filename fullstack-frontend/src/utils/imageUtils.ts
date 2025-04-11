import { computed } from 'vue';

const API_BASE_URL = 'http://localhost:8080';

export const getImageUrl = (imagePath: string): string => {
  if (!imagePath) return '';
  if (imagePath.startsWith('http')) return imagePath;
  
  // Remove any leading slashes and the uploads/ prefix from the image path
  const cleanPath = imagePath.replace(/^\/+/, '').replace(/^uploads\//, '');
  return `${API_BASE_URL}/api/items/images/${cleanPath}`;
};

export const getImageUrls = (imagePaths: string[] | undefined): string[] => {
  if (!imagePaths || !Array.isArray(imagePaths)) return [];
  return imagePaths.map(getImageUrl);
}; 