import axios from '@/axios'

/** 文件上传 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/admin/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
