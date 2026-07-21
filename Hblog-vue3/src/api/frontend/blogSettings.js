import axios from '@/axios'

/** 前台博客设置详情（无需登录） */
export function getBlogSettingsDetail() {
  return axios.post('/blog/settings/detail', {})
}
