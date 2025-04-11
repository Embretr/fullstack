import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useAuthStore } from '@/stores/auth'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('../views/Categories.vue')
  },
  {
    path: '/categories/:id',
    name: 'CategoryItems',
    component: () => import('../views/CategoryItems.vue')
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/Favorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/Messages.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/:itemId/:receiverId/:sellerId/:itemPrice/:itemTitle',
    name: 'Chat',
    component: () => import('../views/ChatView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
  },
  {
    path: '/settings',
    name: 'UserSettings',
    component: () => import('../views/UserSettingsView.vue'),
  },
  {
    path: '/listItem',
    name: 'ListItem',
    component: () => import('../views/CreateItemView.vue'),
  },
  {
    path: '/admin',
    name: 'AdminPanel',
    component: () => import('@/components/AdminPanel.vue'),
  },
  {
    path: '/item/:id',
    name: 'ItemView',
    component: () => import('../views/ItemView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // If the route requires authentication
  if (to.meta.requiresAuth) {
    // Check if user is authenticated
    if (!authStore.isAuthenticated) {
      // Try to initialize auth state
      await authStore.initialize()
      
      // If still not authenticated, redirect to login
      if (!authStore.isAuthenticated) {
        return next({ name: 'Login', query: { redirect: to.fullPath } })
      }
    }
  }
  
  next()
})

export default router