import axios from '@/axios'

/** 首页文章分页 */
export function getArticlePageList(data) {
  return axios.post('/article/list', data)
}

/** 文章详情 */
export function getArticleDetail(data) {
  return axios.post('/article/detail', data)
}

/** 分类下文章分页 */
export function getCategoryArticlePageList(data) {
  return axios.post('/category/article/list', data)
}

/** 标签下文章分页 */
export function getTagArticlePageList(data) {
  return axios.post('/tag/article/list', data)
}
