import { ElMessage, ElMessageBox } from 'element-plus'

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

/**
 * 弹出确认框
 * @param {string} content 提示内容
 * @param {'success' | 'warning' | 'info' | 'error'} type 图标类型
 * @param {string} title 标题
 */
export function showModel(content = '提示内容', type = 'warning', title = '') {
  return ElMessageBox.confirm(content, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type,
  })
}
