<template>
  <div>
    <button @click="openChat" class="chat-button">
      <i class="fas fa-comments"></i> Chat with Seller
    </button>
    
    <div v-if="showChat" class="chat-modal">
      <div class="chat-modal-content">
        <div class="chat-modal-header">
          <h3>Chat with {{ itemOwner?.username }}</h3>
          <button @click="closeChat" class="close-button">&times;</button>
        </div>
        <ChatComponent 
          :item-id="itemId" 
          :receiver-id="itemOwner?.id || 0" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ChatComponent from './ChatComponent.vue'
import type { UserResponse } from '@/api/model/userResponse'

interface Props {
  itemId: number
  itemOwner: UserResponse | null
}

defineProps<Props>()
const showChat = ref(false)

const openChat = () => {
  showChat.value = true
}

const closeChat = () => {
  showChat.value = false
}
</script>

<style scoped>
.chat-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chat-button:hover {
  background-color: #0056b3;
}

.chat-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.chat-modal-content {
  background-color: white;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.chat-modal-header {
  padding: 1rem;
  border-bottom: 1px solid #ccc;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.close-button:hover {
  color: #000;
}
</style> 