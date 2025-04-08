import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as { name: string; email: string } | null,
  }),
  actions: {
    login(user: { name: string; email: string }) {
      this.user = user;
    },
    logout() {
      this.user = null;
    },
  },
  getters: {
    isLoggedIn: (state) => !!state.user,
  },
});