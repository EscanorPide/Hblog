<template>
  <header class="admin-header">
    <div class="header-left">
      <button
        class="collapse-button"
        type="button"
        :aria-label="collapsed ? '展开侧边栏' : '收起侧边栏'"
        @click="$emit('toggle-menu')"
      >
        <el-icon :size="20">
          <Expand v-if="collapsed" />
          <Fold v-else />
        </el-icon>
      </button>

      <div class="breadcrumb">
        <span>Hblog</span>
        <el-icon><ArrowRight /></el-icon>
        <strong>{{ currentTitle }}</strong>
      </div>
    </div>

    <div class="header-actions">
      <a class="site-link" href="/" target="_blank" rel="noopener noreferrer">
        <el-icon><View /></el-icon>
        <span>查看站点</span>
      </a>

      <el-dropdown trigger="click" @command="handleCommand">
        <button class="user-button" type="button">
          <span class="avatar">{{ avatarLetter }}</span>
          <span class="user-name">{{ displayName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="home">
              <el-icon><House /></el-icon>
              后台首页
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import {
  ArrowDown,
  ArrowRight,
  Expand,
  Fold,
  House,
  SwitchButton,
  View,
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['toggle-menu'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentTitle = computed(() => route.meta.title || '后台首页')
const displayName = computed(() => userStore.userInfo?.username || '管理员')
const avatarLetter = computed(() => displayName.value.charAt(0).toUpperCase())

function handleCommand(command) {
  if (command === 'home') {
    router.push('/admin/index')
    return
  }

  if (command === 'logout') {
    userStore.logout()
    showMessage('已退出登录')
    router.replace('/login')
  }
}
</script>

<style scoped>
.admin-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px 0 16px;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e9edf3;
  backdrop-filter: blur(12px);
}

.header-left,
.header-actions,
.breadcrumb,
.site-link,
.user-button {
  display: flex;
  align-items: center;
}

.header-left {
  min-width: 0;
  gap: 18px;
}

.collapse-button {
  width: 38px;
  height: 38px;
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  border: 0;
  border-radius: 10px;
  color: #4b5563;
  background: transparent;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.collapse-button:hover {
  color: #4f46e5;
  background: #f2f3ff;
}

.breadcrumb {
  min-width: 0;
  gap: 8px;
  color: #9ca3af;
  font-size: 14px;
}

.breadcrumb strong {
  overflow: hidden;
  color: #303442;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-actions {
  gap: 20px;
}

.site-link {
  gap: 6px;
  color: #6b7280;
  font-size: 14px;
  text-decoration: none;
}

.site-link:hover {
  color: #4f46e5;
}

.user-button {
  gap: 9px;
  padding: 5px 8px 5px 5px;
  border: 0;
  border-radius: 12px;
  color: #4b5563;
  background: transparent;
  cursor: pointer;
}

.user-button:hover {
  background: #f6f7fb;
}

.avatar {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  font-weight: 700;
  box-shadow: 0 6px 14px rgba(99, 102, 241, 0.25);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 640px) {
  .admin-header {
    padding-right: 12px;
  }

  .breadcrumb span,
  .breadcrumb .el-icon,
  .site-link,
  .user-name {
    display: none;
  }

  .header-actions {
    gap: 6px;
  }
}
</style>
