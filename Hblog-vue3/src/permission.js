import router from '@/router/index'
import { getToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

// 全局路由前置守卫
router.beforeEach(async (to, from, next) => {
  // 若用户想访问后台（以 /admin 为前缀的路由）
  // 未登录，则强制跳转登录页
  const token = getToken()
  if (!token && to.path.startsWith('/admin')) {
    showMessage('请先登录', 'warning')
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (token && to.path === '/login') {
    const userStore = useUserStore()
    if (!userStore.userInfo?.username || !(userStore.roles || []).length) {
      await userStore.setUserInfo()
    }
    showMessage('请勿重复登录', 'warning')
    next(userStore.hasRole('admin', 'editor') ? '/admin/index' : '/')
  } else if (token && to.path.startsWith('/admin')) {
    const userStore = useUserStore()
    // 无用户名或无角色时重新拉取（避免持久化缓存缺 roles 导致菜单消失）
    if (!userStore.userInfo?.username || !(userStore.roles || []).length) {
      await userStore.setUserInfo()
    }
    const requiredRoles = to.meta.roles || []
    if (requiredRoles.length && !requiredRoles.some((role) => userStore.hasRole(role))) {
      showMessage('普通用户无权进入管理后台', 'warning')
      next('/')
      return
    }
    next()
  } else {
    next()
  }
})

// 全局路由后置守卫
router.afterEach((to, from) => {
  // 动态设置页面 Title
  const title = (to.meta.title ? to.meta.title : '') + ' - Weblog'
  document.title = title
})
