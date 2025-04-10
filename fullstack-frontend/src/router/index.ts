import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'

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
    path: '/chat/:itemId/:receiverId',
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
    path: '/userSettings',
    name: 'UserSettings',
    component: () => import('../views/UserSettingsView.vue'),
  },
  {
    path: '/createItem',
    name: 'CreateItem',
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


export default router