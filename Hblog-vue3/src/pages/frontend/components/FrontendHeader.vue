<script setup>
import { ArrowDown, Monitor, Search, SwitchButton } from '@element-plus/icons-vue'
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'
import SearchModal from './SearchModal.vue'
import LogoutConfirm from './LogoutConfirm.vue'

const route = useRoute()
const router = useRouter()
const { settings } = storeToRefs(useBlogSettingsStore())
const userStore = useUserStore()
const { userInfo, nickname: storeNickname } = storeToRefs(userStore)
const searchOpen = ref(false)
const menuOpen = ref(false)
const logoutOpen = ref(false)
const userMenuRef = ref(null)
/** userInfo 变化时重新读 token（cookie 本身非响应式） */
const isLoggedIn = computed(() => Boolean(getToken()))
const canEnterAdmin = computed(() => userStore.hasRole('admin', 'editor'))
const displayName = computed(
  () => storeNickname.value || userInfo.value?.username || '',
)
const avatarChar = computed(() => {
  const text = displayName.value.trim()
  return text ? text.slice(0, 1).toUpperCase() : '…'
})

const blogName = computed(() => settings.value.name || 'Hblog')
const blogLogo = computed(() => settings.value.logo || '')

const navItems = [
  { label: '首页', to: '/' },
  { label: '分类', to: '/category' },
  { label: '标签', to: '/tag' },
  { label: '归档', to: '/archive' },
]

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}

function goAdmin() {
  menuOpen.value = false
  router.push('/admin/index')
}

function askLogout() {
  menuOpen.value = false
  logoutOpen.value = true
}

function doLogout() {
  userStore.logout()
  showMessage('已退出登录')
  router.replace('/')
}

function onDocClick(e) {
  if (menuOpen.value && userMenuRef.value && !userMenuRef.value.contains(e.target)) {
    menuOpen.value = false
  }
}

function onDocKeydown(e) {
  if (e.key === 'Escape' && menuOpen.value) {
    menuOpen.value = false
  }
}

watch(menuOpen, (open) => {
  if (open) {
    document.addEventListener('click', onDocClick)
    document.addEventListener('keydown', onDocKeydown)
  } else {
    document.removeEventListener('click', onDocClick)
    document.removeEventListener('keydown', onDocKeydown)
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick)
  document.removeEventListener('keydown', onDocKeydown)
})

watch(
  isLoggedIn,
  (loggedIn) => {
    if (loggedIn && !userInfo.value?.username) {
      userStore.setUserInfo()
    }
  },
  { immediate: true },
)
</script>

<template>
  <header class="craft-header">
    <div class="craft-header__inner">
      <RouterLink to="/" class="craft-brand">
        <img v-if="blogLogo" :src="blogLogo" class="craft-brand__logo" :alt="blogName" />
        <span v-else class="craft-brand__mark">{{ blogName.slice(0, 1) }}</span>
        <span class="craft-brand__name">{{ blogName }}</span>
      </RouterLink>

      <nav class="craft-nav">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="craft-nav__link"
          :class="{ 'is-active': isActive(item.to) }"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="craft-header__actions">
        <button
          type="button"
          class="craft-search"
          aria-label="搜索文章"
          @click="searchOpen = true"
        >
          <el-icon :size="18"><Search /></el-icon>
        </button>

        <RouterLink v-if="!isLoggedIn" to="/login" class="craft-login">登录</RouterLink>

        <div v-else ref="userMenuRef" class="craft-user-menu">
          <button
            type="button"
            class="craft-user"
            :class="{ 'is-open': menuOpen }"
            aria-haspopup="menu"
            :aria-expanded="menuOpen"
            @click="toggleMenu"
          >
            <span class="craft-user__avatar" aria-hidden="true">{{ avatarChar }}</span>
            <span class="craft-user__name">{{ displayName || '加载中…' }}</span>
            <el-icon :size="13" class="craft-user__arrow"><ArrowDown /></el-icon>
          </button>

          <Transition name="craft-menu">
            <div v-if="menuOpen" class="craft-menu" role="menu">
              <div class="craft-menu__hello">
                <p class="craft-menu__hi">你好，{{ displayName || '用户' }}</p>
                <p class="craft-menu__sub">欢迎回来</p>
              </div>
              <button
                v-if="canEnterAdmin"
                type="button"
                class="craft-menu__item"
                role="menuitem"
                @click="goAdmin"
              >
                <el-icon :size="15"><Monitor /></el-icon>
                进入后台
              </button>
              <button
                type="button"
                class="craft-menu__item craft-menu__item--danger"
                role="menuitem"
                @click="askLogout"
              >
                <el-icon :size="15"><SwitchButton /></el-icon>
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>

    <SearchModal v-model="searchOpen" />
    <LogoutConfirm v-model="logoutOpen" @confirm="doLogout" />
  </header>
</template>

<style scoped>
.craft-header {
  position: sticky;
  top: 0;
  z-index: 40;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #efefef;
}

.craft-header__inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0.85rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .craft-header__inner {
    padding: 0.95rem 2rem;
  }
}

.craft-brand {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  min-width: 0;
  color: #0f0f0f;
  text-decoration: none;
}

.craft-brand__logo {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  object-fit: cover;
}

.craft-brand__mark {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: #0f0f0f;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 700;
}

.craft-brand__name {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.craft-nav {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
}

.craft-nav__link {
  font-size: 0.925rem;
  color: #5a5a5a;
  text-decoration: none;
  transition: color 0.15s ease;
}

.craft-nav__link:hover,
.craft-nav__link.is-active {
  color: #0f0f0f;
}

.craft-nav__link.is-active {
  font-weight: 600;
}

.craft-header__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

.craft-search {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #5a5a5a;
  cursor: pointer;
  transition:
    color 0.15s ease,
    background 0.15s ease;
}

.craft-search:hover {
  color: #0f0f0f;
  background: #f5f5f5;
}

.craft-login {
  font-size: 0.925rem;
  color: #5a5a5a;
  text-decoration: none;
}

.craft-login:hover {
  color: #0f0f0f;
}

.craft-user-menu {
  position: relative;
}

.craft-user {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.28rem 0.6rem 0.28rem 0.32rem;
  border: 1px solid transparent;
  border-radius: 999px;
  background: transparent;
  color: #5a5a5a;
  font-size: 0.9rem;
  font-family: inherit;
  cursor: pointer;
  transition:
    color 0.15s ease,
    background 0.15s ease,
    border-color 0.15s ease;
}

.craft-user:hover,
.craft-user.is-open {
  color: #0f0f0f;
  background: #f5f5f5;
  border-color: #ececec;
}

.craft-user__avatar {
  width: 26px;
  height: 26px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #0f0f0f;
  color: #fff;
  font-size: 0.72rem;
  font-weight: 700;
  flex-shrink: 0;
}

.craft-user__name {
  max-width: 7em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.craft-user__arrow {
  color: #a3a3a3;
  transition: transform 0.18s ease;
}

.craft-user.is-open .craft-user__arrow {
  transform: rotate(180deg);
}

.craft-menu {
  position: absolute;
  top: calc(100% + 10px);
  inset-inline-end: 0;
  min-width: 200px;
  padding: 0.4rem;
  border: 1px solid #ececec;
  border-radius: 14px;
  background: #fff;
  box-shadow:
    0 12px 32px rgba(15, 23, 42, 0.1),
    0 2px 8px rgba(15, 23, 42, 0.05);
  z-index: 50;
}

.craft-menu__hello {
  padding: 0.55rem 0.75rem 0.6rem;
  border-bottom: 1px solid #f1f1f1;
  margin-bottom: 0.35rem;
}

.craft-menu__hi {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f0f0f;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.craft-menu__sub {
  margin: 0.15rem 0 0;
  font-size: 0.75rem;
  color: #a3a3a3;
}

.craft-menu__item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.55rem 0.75rem;
  border: 0;
  border-radius: 9px;
  background: transparent;
  color: #3a3a3a;
  font-size: 0.875rem;
  font-family: inherit;
  text-align: start;
  cursor: pointer;
  transition:
    background 0.15s ease,
    color 0.15s ease;
}

.craft-menu__item .el-icon {
  color: #8a8a8a;
  transition: color 0.15s ease;
}

.craft-menu__item:hover {
  background: #f5f5f5;
  color: #0f0f0f;
}

.craft-menu__item:hover .el-icon {
  color: #0f0f0f;
}

.craft-menu__item--danger:hover {
  background: #fef2f2;
  color: #dc2626;
}

.craft-menu__item--danger:hover .el-icon {
  color: #dc2626;
}

.craft-menu-enter-active,
.craft-menu-leave-active {
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
}

.craft-menu-enter-from,
.craft-menu-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

@media (max-width: 640px) {
  .craft-nav {
    gap: 0.85rem;
  }

  .craft-nav__link {
    font-size: 0.85rem;
  }
}
</style>
