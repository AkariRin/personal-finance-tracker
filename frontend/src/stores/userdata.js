import { defineStore } from 'pinia'

export const useUserdataStore = defineStore('userdata', {
  state: () => ({
    token: null,
    id: null,
    name: null,
    balance: 0.00,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    login(token, username, name, balance) {
      this.token = token;
      this.id = username;
      this.name = name;
      this.balance = balance;
    },
    logout() {
      this.token = null;
      this.id = null;
      this.name = null;
      this.balance = 0.00;
    },
    updateName(name) {
      this.name = name;
    },
    updateBalance(balance) {
      this.balance = balance;
    },
  },
  persist: true,
})

