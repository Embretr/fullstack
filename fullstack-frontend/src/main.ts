// Polyfill for SockJS
// biome-ignore lint/suspicious/noExplicitAny: <explanation>
;(window as any).global = window

import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { createApp } from 'vue'
// import { createI18n } from 'vue-i18n' // Removed direct import
import i18n from './i18n' // Import the configured i18n instance
import { setupVueQuery } from './plugins/vue-query'

import { useAuthStore } from '@/stores/auth';

import './assets/main.css'

// Removed i18n messages and detection logic

// Configure axios defaults
axios.defaults.baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
axios.defaults.headers.common['Content-Type'] = 'application/json'
axios.defaults.withCredentials = true

// Add request interceptor to handle Authorization header
axios.interceptors.request.use((config) => {
  const cookies = document.cookie.split(';')
  const authCookie = cookies.find(cookie => cookie.trim().startsWith('Authorization='))
  if (authCookie) {
    const token = authCookie.split('=')[1]
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

// Create Vue app instance
const app = createApp(App)

// Create and use Pinia store
const pinia = createPinia()
app.use(pinia)


const authStore = useAuthStore();
authStore.initialize();


// Use router
app.use(router)

// Use i18n plugin
app.use(i18n) // Use the imported i18n instance

// Setup Vue Query
setupVueQuery(app)

// Initialize auth and then mount the app
authStore.initialize().finally(() => {
  app.mount('#app')
})