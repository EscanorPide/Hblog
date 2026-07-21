import axios from '@/axios'

/** 获取文章分页数据 */
export function getArticlePageList(data) {
  return axios.post('/admin/article/list', data)
}

/** 发布文章 */
export function publishArticle(data) {
  return axios.post('/admin/article/publish', data)
}

/** 查询文章详情 */
export function getArticleDetail(data) {
  return axios.post('/admin/article/detail', data)
}

/** 更新文章 */
export function updateArticle(data) {
  return axios.post('/admin/article/update', data)
}

/** 删除文章 */
export function deleteArticle(data) {
  return axios.post('/admin/article/delete', data)
}
