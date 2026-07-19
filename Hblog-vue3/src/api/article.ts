import { request } from './request'

export interface Article {
  id: number
  title: string
  summary?: string
  createTime?: string
}

/** 文章相关接口占位，对接后端时再补全 */
export function fetchArticleList() {
  return request<Article[]>('/articles')
}
