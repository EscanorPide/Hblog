import axios from '@/axios'

// 登录接口
export function login(username, password) {
  return axios.post('/login', { username, password })
}

// 获取登录用户信息
export function getUserInfo() {
  return axios.post('/admin/user/info')
}

// 修改管理员密码
export function updateAdminPassword(username, password) {
  return axios.post('/admin/password/update', { username, password })
}
