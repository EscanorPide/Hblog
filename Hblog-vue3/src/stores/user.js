import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken } from '@/composables/auth'
import { useTagsView } from '@/composables/tagsView'

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

    // 退出登录：清除 token、用户信息持久化缓存、标签页缓存
    function logout() {
      removeToken()
      userInfo.value = {}
      // 清除 pinia 持久化缓存
      localStorage.removeItem('user')
      // 重置标签页
      useTagsView().resetToAffixed()
    }

    return { userInfo, setUserInfo, logout }
  },
  {
    // 开启持久化
    persist: true,
  },
)
