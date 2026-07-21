import axios from '@/axios'

/** 后台评论分页列表 */
export function getCommentPageList(data) {
  return axios.post('/admin/comment/list', data)
}

/** 审核评论：2 通过，3 驳回 */
export function auditComment(data) {
  return axios.post('/admin/comment/audit', data)
}

/** 软删除评论 */
export function deleteComment(data) {
  return axios.post('/admin/comment/delete', data)
}
