import axios from '@/axios'

/** 提交评论（默认进入待审核状态） */
export function addComment(data) {
  return axios.post('/comment/add', data)
}

/** 获取指定路由下已审核通过的评论 */
export function getCommentPageList(data) {
  return axios.post('/comment/list', data)
}
