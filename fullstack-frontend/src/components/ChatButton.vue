<template>
  <div>
    <Button @click="openChat" variant="primary" size="medium">
      <i class="fas fa-comments"></i> Chat with Seller
    </Button>
    
    <div v-if="showChat" class="chat-modal">
      <div class="chat-modal-content">
        <div class="chat-modal-header">
          <h3>Chat with {{ itemOwner?.username }}</h3>
          <Button @click="closeChat" variant="text" class="close-button">×</Button>
        </div>
        <ChatComponent 
          :item-id="itemId" 
          :receiver-id="itemOwner?.id || 0"
          :item-price="itemPrice"
          :item-title="itemTitle"
          :seller-id="itemOwner?.id || 0"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ChatComponent from './ChatComponent.vue'
import type { UserResponse } from '@/api/model/userResponse'
import Button from './common/Button.vue'

interface Props {
  itemId: number
  itemOwner: UserResponse | null
  itemPrice: number
  itemTitle: string
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
  font-size: 1.5rem;
  padding: 0.25rem;
  line-height: 1;
}

.chat-button {
  display: none;
}
</style> 