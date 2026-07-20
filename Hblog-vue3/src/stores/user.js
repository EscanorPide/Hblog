import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken } from '@/composables/auth'

export const useUserStore = defineStore(
  'user',
  () => {
    // 用户信息
    const userInfo = ref({})

    // 设置用户信息
    function setUserInfo() {
      return getUserInfo()
        .then((res) => {
          if (res.success === true) {
            userInfo.value = res.data
          }
        })
        .catch((err) => {
          console.error('获取用户信息失败', err)
        })
    }

    // 退出登录
    function logout() {
      removeToken()
      userInfo.value = {}
    }

    return { userInfo, setUserInfo, logout }
  },
  {
    // 开启持久化
    persist: true,
  },
)
