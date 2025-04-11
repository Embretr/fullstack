import { defineStore } from 'pinia'
import { login, register, logout, getMe } from '../api/authentication/authentication'

interface User {
  id: number
  username: string
  email: string
  fullName: string | null
  roles: string[]
}

interface LoginCredentials {
  username: string
  email: string
  password: string
}

interface RegisterData {
  username: string
  email: string
  password: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    loading: false,
    error: null as string | null
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    userRoles: (state) => state.user?.roles || []
  },

  actions: {
    async login(credentials: LoginCredentials) {
      this.loading = true
      this.error = null
      try {
        await login(credentials)
        await this.fetchUser()
      } catch (error: unknown) {
        this.error = error instanceof Error ? error.message : 'Login failed'
        throw error
      } finally {
        this.loading = false
      }
    },

    async register(data: RegisterData) {
      this.loading = true
      this.error = null
      try {
        await register(data)
        await this.fetchUser()
      } catch (error: unknown) {
        this.error = error instanceof Error ? error.message : 'Registration failed'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchUser() {
      this.loading = true
      try {
        const response = await getMe()
        const userData = response.data
        if (!userData.id || !userData.username || !userData.email) {
          throw new Error('Invalid user data received')
        }
        this.user = {
          id: userData.id,
          username: userData.username,
          email: userData.email,
          fullName: null,
          roles: [userData.role || 'USER']
        }
      } catch (error: unknown) {
        this.error = error instanceof Error ? error.message : 'Failed to fetch user data'
        throw error
      } finally {
        this.loading = false
      }
    },

    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.user = null
        this.error = null
      }
    },

    async initialize() {
      try {
        const response = await getMe();
        const userData = response.data;
        if (!userData.id || !userData.username || !userData.email) {
          throw new Error('Invalid user data received');
        }
        this.user = {
          id: userData.id,
          username: userData.username,
          email: userData.email,
          fullName: null,
          roles: [userData.role || 'USER']
        };
      } catch (error) {
        console.error('Failed to initialize auth state:', error);
        this.user = null;
        this.error = null;
      }
    }
  }
})