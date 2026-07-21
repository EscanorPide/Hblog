import axios from '@/axios'

// 登录接口
export function login(username, password) {
  return axios.post('/login', { username, password })
}

// 公开注册
export function register(data) {
  return axios.post('/register', data)
}

// 获取登录用户信息
export function getUserInfo() {
  return axios.post('/admin/user/info')
}

// 修改管理员密码
export function updateAdminPassword(username, password) {
  return axios.post('/admin/password/update', { username, password })
}

export function getUserPageList(data) {
  return axios.post('/admin/user/manage/list', data)
}

export function createUser(data) {
  return axios.post('/admin/user/manage/create', data)
}

export function updateUserRoles(data) {
  return axios.post('/admin/user/manage/roles/update', data)
}

export function resetUserPassword(data) {
  return axios.post('/admin/user/manage/password/reset', data)
}

export function deleteUser(data) {
  return axios.post('/admin/user/manage/delete', data)
}
