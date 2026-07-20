import axios from '@/axios'

/** 标签分页列表 */
export function getTagPageList(data) {
  return axios.post('/admin/tag/list', data)
}

/** 新增标签 */
export function addTag(data) {
  return axios.post('/admin/tag/add', data)
}

/** 删除标签 */
export function deleteTag(data) {
  return axios.post('/admin/tag/delete', data)
}

/** 标签下拉选项 */
export function getTagSelectList() {
  return axios.post('/admin/tag/select/list')
}
