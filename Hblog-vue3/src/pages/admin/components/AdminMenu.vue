<template>
  <aside class="admin-menu" :class="{ collapsed }">
    <router-link class="brand" to="/admin/index" :aria-label="`${blogName} 后台首页`">
      <img v-if="blogLogo" :src="blogLogo" class="brand-logo" :alt="blogName" />
      <span v-else class="brand-mark">H</span>
      <span class="brand-name">{{ blogName }}</span>
    </router-link>

    <nav class="menu-list" aria-label="后台导航">
      <template v-for="group in menuGroups" :key="group.title">
        <p class="menu-group-title">{{ group.title }}</p>
        <component
          v-for="item in group.items"
          :key="item.path"
          :is="hasRoute(item.path) ? RouterLink : 'button'"
          class="menu-item"
          :class="{ active: isActive(item.path) }"
          :to="hasRoute(item.path) ? item.path : undefined"
          :type="hasRoute(item.path) ? undefined : 'button'"
          :title="collapsed ? item.label : undefined"
          @click="handleMenuClick(item)"
        >
          <el-icon :size="19"><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </component>
      </template>
    </nav>

    <div class="menu-bottom">
      <div class="status-dot"></div>
      <div class="status-copy">
        <strong>系统运行正常</strong>
        <span>Hblog v1.0.0</span>
      </div>
    </div>
  </aside>
</template>

<script setup>
import {
  ChatDotRound,
  CollectionTag,
  DataAnalysis,
  Document,
  FolderOpened,
  House,
  Setting,
  User,
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { showMessage } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { useUserStore } from '@/stores/user'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()
const router = useRouter()
const blogSettingsStore = useBlogSettingsStore()
const userStore = useUserStore()

const blogLogo = computed(() => blogSettingsStore.settings?.logo || '')
const blogName = computed(() => blogSettingsStore.settings?.name || 'Hblog')

const rawMenuGroups = [
  {
    title: '工作台',
    items: [
      { label: '首页概览', path: '/admin/index', icon: House },
      { label: '数据统计', path: '/admin/statistics', icon: DataAnalysis },
    ],
  },
  {
    title: '内容管理',
    items: [
      { label: '文章管理', path: '/admin/articles', icon: Document },
      { label: '分类管理', path: '/admin/categories', icon: FolderOpened },
      { label: '标签管理', path: '/admin/tags', icon: CollectionTag },
      { label: '评论管理', path: '/admin/comments', icon: ChatDotRound },
    ],
  },
  {
    title: '系统',
    items: [
      { label: '用户管理', path: '/admin/users', icon: User, roles: ['admin'] },
      { label: '系统设置', path: '/admin/settings', icon: Setting },
    ],
  },
]

const menuGroups = computed(() => {
  return rawMenuGroups
    .map((group) => ({
      ...group,
      items: group.items.filter(
        (item) => !item.roles?.length || item.roles.some((role) => userStore.hasRole(role)),
      ),
    }))
    .filter((group) => group.items.length > 0)
})

function isActive(path) {
  return route.path === path || (path !== '/admin/index' && route.path.startsWith(`${path}/`))
}

function hasRoute(path) {
  return router.resolve(path).matched.length > 0
}

function handleMenuClick(item) {
  if (!hasRoute(item.path)) showMessage(`${item.label}功能正在建设中`, 'info')
}
</script>

<style scoped>
.admin-menu {
  width: 238px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #aeb4c4;
  background: #151824;
  transition: width 0.25s ease;
}

.admin-menu.collapsed {
  width: 76px;
}

.brand {
  height: 64px;
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 11px;
  padding: 0 20px;
  color: #fff;
  text-decoration: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.brand-mark {
  width: 36px;
  height: 36px;
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 11px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  font-size: 19px;
  font-weight: 800;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.28);
}

.brand-logo {
  width: 36px;
  height: 36px;
  flex: 0 0 auto;
  object-fit: cover;
  border-radius: 11px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.28);
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.02em;
  white-space: nowrap;
}

.menu-list {
  flex: 1;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 12px;
}

.menu-group-title {
  height: 28px;
  margin: 10px 10px 4px;
  overflow: hidden;
  color: #656c80;
  font-size: 11px;
  line-height: 28px;
  letter-spacing: 0.12em;
  white-space: nowrap;
  text-transform: uppercase;
}

.menu-item {
  width: 100%;
  height: 44px;
  display: flex;
  align-items: center;
  gap: 13px;
  margin: 3px 0;
  padding: 0 13px;
  border: 0;
  border-radius: 10px;
  color: #aeb4c4;
  font-size: 14px;
  text-decoration: none;
  white-space: nowrap;
  transition: background 0.2s, color 0.2s;
}

.menu-item .el-icon {
  flex: 0 0 auto;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

.menu-item.active {
  color: #fff;
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.9), rgba(124, 92, 246, 0.82));
  box-shadow: 0 8px 18px rgba(67, 56, 202, 0.22);
}

.menu-bottom {
  min-height: 68px;
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 11px;
  margin: 12px;
  padding: 12px;
  overflow: hidden;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
}

.status-dot {
  width: 9px;
  height: 9px;
  flex: 0 0 auto;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 0 5px rgba(52, 211, 153, 0.1);
}

.status-copy {
  display: flex;
  flex-direction: column;
  white-space: nowrap;
}

.status-copy strong {
  color: #d9dce5;
  font-size: 12px;
  font-weight: 500;
}

.status-copy span {
  color: #656c80;
  font-size: 11px;
}

.collapsed .brand {
  padding: 0 20px;
}

.collapsed .brand-name,
.collapsed .menu-group-title,
.collapsed .menu-item span,
.collapsed .status-copy {
  display: none;
}

.collapsed .menu-group-title {
  height: 8px;
  margin: 0;
}

.collapsed .menu-item {
  justify-content: center;
  padding: 0;
}

.collapsed .menu-bottom {
  min-height: 44px;
  justify-content: center;
  padding: 0;
}

@media (max-width: 800px) {
  .admin-menu:not(.collapsed) {
    position: fixed;
    z-index: 30;
    box-shadow: 12px 0 35px rgba(15, 23, 42, 0.2);
  }

  .admin-menu.collapsed {
    width: 0;
  }
}
</style>
