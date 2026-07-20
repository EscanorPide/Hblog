import { ElMessage } from 'element-plus'

/**
 * 全局消息提示封装
 * @param {string} message 提示内容
 * @param {'success' | 'warning' | 'info' | 'error'} type 消息类型
 * @param {boolean} plain 是否纯色风格
 */
export function showMessage(message = '提示内容', type = 'success', plain = false) {
  return ElMessage({
    message,
    type,
    plain,
    duration: 3000,
  })
}
