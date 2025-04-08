import { createPinia } from 'pinia'
import { markRaw } from 'vue'
import router from '@/router'

const pinia = createPinia()

// Add router to Pinia store instances
pinia.use(({ store }) => {
  store.router = markRaw(router)
})

export default pinia 