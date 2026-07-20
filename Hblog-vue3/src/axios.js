import axios from 'axios'
import { getToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'

// 创建 Axios 实例
const instance = axios.create({
  baseURL: '/api', // 你的 API 基础 URL
  timeout: 7000, // 请求超时时间
})

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
    // 对请求错误做些什么
    return Promise.reject(error)
  },
)

// 添加响应拦截器
instance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response.data
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    const errorMsg = error.response?.data?.message || '请求失败'
    showMessage(errorMsg, 'error')
    return Promise.reject(error)
  },
)

// 暴露出去
export default instance
