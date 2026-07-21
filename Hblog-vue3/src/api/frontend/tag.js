import axios from '@/axios'

/** 侧栏标签列表 */
export function getTagList() {
  return axios.post('/tag/list', {})
}
