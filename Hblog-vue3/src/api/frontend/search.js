import axios from '@/axios'

/** 文章全文搜索（Lucene） */
export function getSearchResult(data) {
  return axios.post('/article/search', data)
}
