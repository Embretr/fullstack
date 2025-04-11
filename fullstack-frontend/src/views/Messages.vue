<template>
  <div class="messages">
    <h1>{{ $t('messagesView.title') }}</h1>
    <div v-if="isLoading" class="loading">
      <p>{{ $t('common.loading') }}</p>
    </div>
    <div v-else-if="!messages?.data?.length" class="empty-state">
      <p>{{ $t('messagesView.emptyState') }}</p>
    </div>
    <div v-else class="messages-list">
      <div v-for="message in messages.data" :key="message.id" class="message-card">
        <div class="message-header">
          <h3>{{ message.sender?.username }}</h3>
          <span class="timestamp">{{ message.timestamp ? formatDate(message.timestamp) : '' }}</span>
        </div>
        <p class="message-content">{{ message.content }}</p>
        <div v-if="message.item" class="message-item">
          <p class="item-title">{{ message.item.title }}</p>
          <p class="item-price">{{ message.item.price ? formatPrice(message.item.price) : '' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGetUserConversations } from '@/api/messages/messages';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const { data: messages, isLoading } = useGetUserConversations();

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString();
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(price);
};
</script>

<style scoped>
.messages {
  padding: 2rem;
}

.loading, .empty-state {
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

.message-item {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.item-title {
  font-weight: 500;
  color: var(--secondary-color);
}

.item-price {
  color: var(--primary-color);
  font-weight: 600;
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