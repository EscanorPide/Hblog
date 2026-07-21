import axios from 'axios'
import { getToken, removeToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'
import router from '@/router'

// 创建 Axios 实例
const instance = axios.create({
  baseURL: '/api', // 你的 API 基础 URL
  timeout: 7000, // 请求超时时间
})

/** 未授权业务码（与后端 ResponseCodeEnum.UNAUTHORIZED 对齐） */
const UNAUTHORIZED_CODE = '20002'

/** 避免并发 401 重复弹窗 / 跳转 */
let forcingLogout = false

function forceLogoutToLogin(message = '登录已过期，请重新登录') {
  if (forcingLogout) return
  forcingLogout = true

  removeToken()

  // 动态引入，避免 axios ↔ store ↔ api 循环依赖
  import('@/stores/user')
    .then(({ useUserStore }) => {
      useUserStore().logout()
    })
    .catch(() => {})

  showMessage(message, 'warning')

  const current = router.currentRoute.value
  if (current.path !== '/login') {
    router
      .replace({
        path: '/login',
        query: { redirect: current.fullPath },
      })
      .finally(() => {
        forcingLogout = false
      })
  } else {
    forcingLogout = false
  }
}

function isUnauthorizedPayload(data) {
  return data?.errorCode === UNAUTHORIZED_CODE
}

// 添加请求拦截器
instance.interceptors.request.use(
  function (config) {
    // 登录后给请求头带上 Token（后端要求：Authorization: Bearer xxx）
    const token = getToken()
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  function (error) {
    return Promise.reject(error)
  },
)

// 添加响应拦截器
instance.interceptors.response.use(
  function (response) {
    const data = response.data

    // 少数接口可能 HTTP 200 但业务码表示未登录
    if (isUnauthorizedPayload(data)) {
      forceLogoutToLogin(data.message || '无访问权限，请先登录！')
      return Promise.reject(data)
    }

    return data
  },
  function (error) {
    const status = error.response?.status
    const data = error.response?.data

    // token 失效 / 未登录：强制退出并跳转登录页
    if (status === 401 || isUnauthorizedPayload(data)) {
      forceLogoutToLogin(data?.message || '登录已过期，请重新登录')
      return Promise.reject(error)
    }

    const errorMsg = data?.message || '请求失败'
    showMessage(errorMsg, 'error')
    return Promise.reject(error)
  },
)

// 暴露出去
export default instance
