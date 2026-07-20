import axios from '@/axios'

/** 分类分页列表 */
export function getCategoryPageList(data) {
  return axios.post('/admin/category/list', data)
}

/** 新增分类 */
export function addCategory(data) {
  return axios.post('/admin/category/add', data)
}

/** 删除分类 */
export function deleteCategory(data) {
  return axios.post('/admin/category/delete', data)
}

/** 分类下拉选项 */
export function getCategorySelectList() {
  return axios.post('/admin/category/select/list')
}
