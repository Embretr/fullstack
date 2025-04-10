<template>
  <div class="vipps-payment">
    <button @click="initiatePaymentHandler" class="vipps-button" :disabled="isLoading">
      <img :src="vippsLogo" alt="Vipps" class="vipps-logo" />
      {{ isLoading ? 'Processing...' : 'Pay with Vipps' }}
    </button>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import vippsLogo from '../assets/vipps-logo.png';
import { useInitiatePayment } from '@/api/vipps-controller/vipps-controller'
import type { InitiatePaymentParams } from '@/api/model/initiatePaymentParams'

interface Props {
  orderId: string;
  amount: number;
  description: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'paymentInitiated', url: string): void;
  (e: 'paymentError', error: string): void;
}>();

const isLoading = ref(false);
const error = ref<string | null>(null);

const { mutate: initiatePayment } = useInitiatePayment({
  mutation: {
    onSuccess: (response) => {
      const url = response?.data?.url;
      if (typeof url === 'string') {
        emit('paymentInitiated', url);
      } else {
        error.value = 'Invalid response format from payment initiation';
        emit('paymentError', error.value);
      }
    },
    onError: (err) => {
      error.value = err instanceof Error ? err.message : 'Failed to initiate payment';
      emit('paymentError', error.value);
    }
  }
});

const initiatePaymentHandler = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    await initiatePayment({
      params: {
        orderId: props.orderId,
        amount: props.amount,
        description: props.description
      }
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.vipps-payment {
  margin: 1rem 0;
}

.vipps-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #FF5B24;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s;
  width: 100%;
}

.vipps-button:hover:not(:disabled) {
  background-color: #E54A1A;
}

.vipps-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.vipps-logo {
  height: 24px;
  width: auto;
}

.error-message {
  color: #dc3545;
  margin-top: 0.5rem;
  font-size: 0.875rem;
}
</style> 