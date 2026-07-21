import axios from '@/axios'

/** 从上传接口响应中取出可访问 URL */
export function resolveUploadUrl(res) {
  const data = res?.data
  if (!data) return ''
  if (typeof data === 'string') return data
  return data.url || data.fileUrl || data.path || ''
}

/** 文件上传 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/admin/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000,
  })
}
