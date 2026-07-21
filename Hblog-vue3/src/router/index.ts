import FrontendLayout from '@/pages/frontend/FrontendLayout.vue'
import Index from '@/pages/frontend/index.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import Archive from '@/pages/frontend/archive.vue'
import Login from '@/pages/admin/login.vue'
import AdminLayout from '@/pages/admin/AdminLayout.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticles from '@/pages/admin/articles.vue'
import AdminCategories from '@/pages/admin/categories.vue'
import AdminTags from '@/pages/admin/tags.vue'
import AdminSettings from '@/pages/admin/settings.vue'
import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: FrontendLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: Index,
        meta: { title: '首页' },
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: ArticleDetail,
        meta: { title: '文章详情' },
      },
      {
        path: 'category/:id?',
        name: 'CategoryList',
        component: CategoryList,
        meta: { title: '分类' },
      },
      {
        path: 'tag/:id?',
        name: 'TagList',
        component: TagList,
        meta: { title: '标签' },
      },
      {
        path: 'archive',
        name: 'Archive',
        component: Archive,
        meta: { title: '归档' },
      },
    ],
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
      {
        path: 'settings',
        name: 'AdminSettings',
        component: AdminSettings,
        meta: {
          title: '系统设置',
        },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router
