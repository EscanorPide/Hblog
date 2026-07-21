import axios from '@/axios'

/** 获取仪表盘基础信息（文章数、分类数、标签数、总浏览量） */
export function getBaseStatisticsInfo() {
  return axios.post('/admin/dashboard/statistics')
}

/** 获取文章发布热点统计（近一年每日发布数） */
export function getPublishArticleStatisticsInfo() {
  return axios.post('/admin/dashboard/publishArticle/statistics')
}

/** 获取近一周 PV 访问量统计 */
export function getPvStatisticsInfo() {
  return axios.post('/admin/dashboard/pv/statistics')
}
