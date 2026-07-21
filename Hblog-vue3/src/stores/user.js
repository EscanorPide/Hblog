import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken } from '@/composables/auth'
import { useTagsView } from '@/composables/tagsView'

/** 统一角色：ROLE_ADMIN / Admin -> admin */
function normalizeRoles(roles) {
  if (!Array.isArray(roles)) return []
  return roles
    .map((role) =>
      String(role || '')
        .trim()
        .toLowerCase()
        .replace(/^role_/, ''),
    )
    .filter(Boolean)
}

export const useUserStore = defineStore(
  'user',
  () => {
    // 用户信息
    const userInfo = ref({})

    const roles = computed(() => normalizeRoles(userInfo.value?.roles))

    const isLoggedIn = computed(() => Boolean(userInfo.value?.username))

    /** 评论展示用昵称 */
    const nickname = computed(
      () => userInfo.value?.nickname || userInfo.value?.username || '',
    )

    /** 评论用邮箱（库里暂无邮箱字段时用占位，满足后端校验，总长 ≤ 60） */
    const mail = computed(() => {
      if (userInfo.value?.mail || userInfo.value?.email) {
        return userInfo.value.mail || userInfo.value.email
      }
      const name = String(userInfo.value?.username || '').trim()
      if (!name) return ''
      const local = name.slice(0, 48)
      return `${local}@hblog.local`
    })

    function hasRole(...required) {
      const need = required.map((r) => String(r).toLowerCase().replace(/^role_/, ''))
      return need.some((r) => roles.value.includes(r))
    }

    // 设置用户信息
    function setUserInfo() {
      return getUserInfo()
        .then((res) => {
          if (res.success === true) {
            const data = res.data || {}
            userInfo.value = {
              ...data,
              roles: normalizeRoles(data.roles),
            }
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

    return {
      userInfo,
      roles,
      isLoggedIn,
      nickname,
      mail,
      hasRole,
      setUserInfo,
      logout,
    }
  },
  {
    // 开启持久化
    persist: true,
  },
)
