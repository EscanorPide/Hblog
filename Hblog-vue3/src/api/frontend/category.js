import axios from '@/axios'

/** 侧栏分类列表 */
export function getCategoryList() {
  return axios.post('/category/list', {})
}
