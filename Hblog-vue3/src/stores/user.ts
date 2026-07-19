import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(null)
  const nickname = ref('')

  function setToken(value: string | null) {
    token.value = value
  }

  function setNickname(value: string) {
    nickname.value = value
  }

  function logout() {
    token.value = null
    nickname.value = ''
  }

  return {
    token,
    nickname,
    setToken,
    setNickname,
    logout,
  }
})
