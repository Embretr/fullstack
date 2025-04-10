<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-messages" ref="messagesContainer">
        <div v-for="message in messages" :key="message.id" 
             :class="['message', message.sender?.id === currentUser?.id ? 'sent' : 'received']">
          <div class="message-content">
            <div class="message-sender">{{ message.sender?.username }}</div>
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.timestamp ? formatTime(message.timestamp) : '' }}</div>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import SockJS from 'sockjs-client'
import type { IMessage } from '@stomp/stompjs'
import { Client } from '@stomp/stompjs'
import { useRoute } from 'vue-router'
import { useGetConversation } from '@/api/message-controller/message-controller'
import type { Message } from '@/api/model/message'

const route = useRoute()
const itemId = Number(route.params.itemId)
const receiverId = Number(route.params.receiverId)

const authStore = useAuthStore()
const currentUser = authStore.user
const messages = ref<Message[]>([])
const newMessage = ref('')
const isConnected = ref(false)
const stompClient = ref<Client | null>(null)
const messagesContainer = ref<HTMLElement | null>(null)

const connect = () => {
  console.log('Attempting to connect to WebSocket...')
  
  const socket = new SockJS('http://localhost:8080/ws', null, {
    transports: ['websocket', 'xhr-streaming', 'xhr-polling']
  })
  
  stompClient.value = new Client({
    webSocketFactory: () => socket,
    onConnect: () => {
      console.log('Successfully connected to WebSocket')
      isConnected.value = true
      subscribeToMessages()
    },
    onDisconnect: () => {
      console.log('Disconnected from WebSocket')
      isConnected.value = false
    },
    onStompError: (frame) => {
      console.error('STOMP error:', frame)
    },
    debug: (str) => {
      console.log(`STOMP: ${str}`)
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000
  })
  
  stompClient.value.activate()
}

const subscribeToMessages = () => {
  if (stompClient.value) {
    const topic = `/topic/chat/${itemId}/${receiverId}`
    console.log('Subscribing to topic:', topic)
    stompClient.value.subscribe(topic, (message: IMessage) => {
      console.log('Received message:', message)
      const receivedMessage = JSON.parse(message.body) as Message
      messages.value = [...messages.value, receivedMessage]
      scrollToBottom()
    })
  }
}

const sendMessage = () => {
  if (!newMessage.value.trim() || !stompClient.value) return

  const message = {
    content: newMessage.value,
    receiver: { id: receiverId },
    item: { id: itemId }
  }

  console.log('Sending message:', message)
  try {
    stompClient.value.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(message),
      headers: {
        'content-type': 'application/json'
      }
    })
    newMessage.value = ''
  } catch (error) {
    console.error('Error sending message:', error)
  }
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
    const { data, error } = useGetConversation(itemId, receiverId)
    watch(data, (newData) => {
      if (newData?.data) {
        console.log('Loaded messages:', newData.data)
        messages.value = newData.data
        scrollToBottom()
      }
    }, { immediate: true })

    if (error.value) {
      console.error('Error loading messages:', error.value)
    }
  } catch (error) {
    console.error('Error in loadMessages:', error)
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
.chat-page {
  height: 100vh;
  padding: 2rem;
  background-color: #f5f5f5;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 800px;
  margin: 0 auto;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
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
  padding: 0.75rem 1.25rem;
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
  padding: 1.5rem;
  border-top: 1px solid #eee;
  background-color: white;
}

.chat-input input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-right: 0.75rem;
  font-size: 1rem;
}

.chat-input button {
  padding: 0.75rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.chat-input button:hover {
  background-color: #0056b3;
}

.chat-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style> 