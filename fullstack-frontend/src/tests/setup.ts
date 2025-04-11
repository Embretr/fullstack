import { config } from '@vue/test-utils'
import { createI18n } from 'vue-i18n'
import { createRouter, createWebHistory } from 'vue-router' // Import router functions
import { h } from 'vue' // Import h function for dummy component
import { VueQueryPlugin, QueryClient } from '@tanstack/vue-query'

// Create a QueryClient instance for testing
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
  },
})

// Dummy component for the root route
const DummyComponent = h('div')

// Create a mock router instance
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: DummyComponent } // Add route for root path
    ],
})

// Create a basic i18n instance for testing
const i18n = createI18n({
    legacy: false, // Use Composition API mode
    locale: 'en', // Set default locale
    messages: {
        en: {
            'userProfile.createItemButton': 'Create Item' // Add the missing key
        }
    },
    // Suppress warnings about missing translations during tests
    silentTranslationWarn: true,
    silentFallbackWarn: true,
})

// Apply the i18n, router, and Vue Query instances globally to Vue Test Utils
config.global.plugins = [i18n, router, [VueQueryPlugin, { queryClient }]] // Add router and Vue Query to plugins
