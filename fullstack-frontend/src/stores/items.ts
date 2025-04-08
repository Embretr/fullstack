import { defineStore } from 'pinia'
import axios from 'axios'

interface Item {
  id: number
  title: string
  description: string
  price: number
  location: string
  image: string
  seller: {
    id: number
    name: string
  }
  category: {
    id: number
    name: string
  }
  createdAt: string
}

interface ItemsState {
  items: Item[]
  loading: boolean
  error: string | null
  favorites: number[]
}

export const useItemsStore = defineStore('items', {
  state: (): ItemsState => ({
    items: [],
    loading: false,
    error: null,
    favorites: []
  }),

  getters: {
    getFavoriteItems: (state) => {
      return state.items.filter(item => state.favorites.includes(item.id))
    },
    getItemsByCategory: (state) => {
      return (categoryId: number) => state.items.filter(item => item.category.id === categoryId)
    }
  },

  actions: {
    async fetchItems() {
      this.loading = true
      try {
        const response = await axios.get('/api/items')
        this.items = response.data
        this.error = null
      } catch (error) {
        this.error = 'Failed to fetch items'
        console.error('Error fetching items:', error)
      } finally {
        this.loading = false
      }
    },

    async addItem(item: Omit<Item, 'id' | 'createdAt' | 'seller'>) {
      this.loading = true
      try {
        const response = await axios.post('/api/items', item)
        this.items.push(response.data)
        this.error = null
      } catch (error) {
        this.error = 'Failed to add item'
        console.error('Error adding item:', error)
      } finally {
        this.loading = false
      }
    },

    toggleFavorite(itemId: number) {
      const index = this.favorites.indexOf(itemId)
      if (index === -1) {
        this.favorites.push(itemId)
      } else {
        this.favorites.splice(index, 1)
      }
      // Persist favorites to localStorage
      localStorage.setItem('favorites', JSON.stringify(this.favorites))
    },

    loadFavorites() {
      const savedFavorites = localStorage.getItem('favorites')
      if (savedFavorites) {
        this.favorites = JSON.parse(savedFavorites)
      }
    }
  }
}) 