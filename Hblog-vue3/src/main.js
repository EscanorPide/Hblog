import '@/assets/styles/main.css'
// Element Plus 消息提示样式（函数式调用不会自动按需引入样式）
import 'element-plus/es/components/message/style/css'

import { createApp } from 'vue'
import App from '@/App.vue'
// 引入全局状态管理 Pinia
import pinia from '@/stores'

// 导入路由
import router from '@/router'
// 导入权限路由守卫
import '@/permission'

const app = createApp(App)

app.use(pinia)
// 应用路由
app.use(router)
app.mount('#app')
