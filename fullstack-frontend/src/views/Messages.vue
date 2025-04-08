<template>
  <div class="messages">
    <h1>{{ $t('messagesView.title') }}</h1>
    <div v-if="messages.length === 0" class="empty-state">
      <p>{{ $t('messagesView.emptyState') }}</p>
    </div>
    <div v-else class="messages-list">
      <div v-for="message in messages" :key="message.id" class="message-card">
        <div class="message-header">
          <h3>{{ message.sender }}</h3>
          <span class="timestamp">{{ message.timestamp }}</span>
        </div>
        <p class="message-content">{{ message.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Message {
  id: number;
  sender: string;
  content: string;
  timestamp: string;
}

const messages: Message[] = [];
</script>

<style scoped>
.messages {
  padding: 2rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: var(--border-radius);
  margin-top: 2rem;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.message-card {
  background: white;
  padding: 1.5rem;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

h1 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

h3 {
  color: var(--secondary-color);
}

.timestamp {
  color: #666;
  font-size: 0.9rem;
}

.message-content {
  color: #333;
}
</style> 