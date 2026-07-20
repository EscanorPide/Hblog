import Index from '@/pages/frontend/index.vue'
import Login from '@/pages/admin/login.vue'
import AdminLayout from '@/pages/admin/AdminLayout.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticles from '@/pages/admin/articles.vue'
import AdminCategories from '@/pages/admin/categories.vue'
import AdminTags from '@/pages/admin/tags.vue'
import { createRouter, createWebHashHistory } from 'vue-router'

// 统一在这里声明所有路由
const routes = [
  {
    path: '/',
    component: Index,
    meta: {
      title: 'Weblog 首页',
    },
  },
  {
    path: '/login',
    component: Login,
    meta: {
      title: 'Weblog 登录页',
    },
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/index',
    children: [
      {
        path: 'index',
        name: 'AdminIndex',
        component: AdminIndex,
        meta: {
          title: '后台首页',
          affix: true,
        },
      },
      {
        path: 'articles',
        name: 'AdminArticles',
        component: AdminArticles,
        meta: {
          title: '文章管理',
        },
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: AdminCategories,
        meta: {
          title: '分类管理',
        },
      },
      {
        path: 'tags',
        name: 'AdminTags',
        component: AdminTags,
        meta: {
          title: '标签管理',
        },
      },
    ],
  },
]

// 创建路由
const router = createRouter({
  // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
  history: createWebHashHistory(),
  // routes: routes 的缩写
  routes,
})

// ES6 模块导出语句，它用于将 router 对象导出，以便其他文件可以导入和使用这个对象
export default router
