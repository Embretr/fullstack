<template>
  <div class="chat-container">
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="message in messages" :key="message.id" 
           :class="['message', message.sender.id === currentUser?.id ? 'sent' : 'received']">
        <div class="message-content">
          <div class="message-sender">{{ message.sender.username }}</div>
          <div class="message-text">{{ message.content }}</div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <input v-model="newMessage" 
             @keyup.enter="sendMessage" 
             placeholder="Type your message..."
             :disabled="!isConnected">
      <button @click="sendMessage" :disabled="!isConnected || !newMessage.trim()">
        Send
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import SockJS from 'sockjs-client'
import type { IMessage } from '@stomp/stompjs'
import { Client } from '@stomp/stompjs'



interface Message {
  id: number
  content: string
  timestamp: string
  sender: {
    id: number
    username: string
  }
  receiver: {
    id: number
  }
  item: {
    id: number
  }
}

const props = defineProps<{
  itemId: number
  receiverId: number
}>()

const authStore = useAuthStore()
const currentUser = authStore.user
const messages = ref<Message[]>([])
const newMessage = ref('')
const isConnected = ref(false)
const stompClient = ref<Client | null>(null)
const messagesContainer = ref<HTMLElement | null>(null)

const connect = () => {
  const socket = new SockJS('http://localhost:8080/ws', null, {
    transports: ['websocket', 'xhr-streaming', 'xhr-polling']
  })
  stompClient.value = new Client({
    webSocketFactory: () => socket,
    onConnect: () => {
      isConnected.value = true
      subscribeToMessages()
    },
    onDisconnect: () => {
      isConnected.value = false
    }
  })
  stompClient.value.activate()
}

const subscribeToMessages = () => {
  if (stompClient.value) {
    stompClient.value.subscribe(`/topic/chat/${props.itemId}/${props.receiverId}`, (message: IMessage) => {
      const receivedMessage = JSON.parse(message.body) as Message
      messages.value.push(receivedMessage)
      scrollToBottom()
    })
  }
}

const sendMessage = () => {
  if (!newMessage.value.trim() || !stompClient.value) return

  const message = {
    content: newMessage.value,
    receiver: { id: props.receiverId },
    item: { id: props.itemId }
  }

  stompClient.value.publish({
    destination: '/app/chat.sendMessage',
    body: JSON.stringify(message)
  })

  newMessage.value = ''
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const loadMessages = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/messages/conversation/${props.itemId}/${props.receiverId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    if (response.ok) {
      messages.value = await response.json()
      scrollToBottom()
    } else {
      console.error('Failed to load messages:', response.statusText)
    }
  } catch (error) {
    console.error('Error loading messages:', error)
  }
}

onMounted(() => {
  loadMessages()
  connect()
})

onUnmounted(() => {
  if (stompClient.value) {
    stompClient.value.deactivate()
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 500px;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  display: flex;
  max-width: 70%;
}

.message.sent {
  align-self: flex-end;
}

.message.received {
  align-self: flex-start;
}

.message-content {
  padding: 0.5rem 1rem;
  border-radius: 1rem;
  background-color: #f0f0f0;
}

.message.sent .message-content {
  background-color: #007bff;
  color: white;
}

.message-sender {
  font-size: 0.8rem;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.message-text {
  word-wrap: break-word;
}

.message-time {
  font-size: 0.7rem;
  text-align: right;
  margin-top: 0.25rem;
  opacity: 0.7;
}

.chat-input {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #ccc;
  background-color: white;
}

.chat-input input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-right: 0.5rem;
}

.chat-input button {
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style> 