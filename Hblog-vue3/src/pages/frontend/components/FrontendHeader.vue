<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { storeToRefs } from 'pinia'

const route = useRoute()
const { settings } = storeToRefs(useBlogSettingsStore())

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
        <RouterLink to="/login" class="craft-login">登录</RouterLink>
        <RouterLink to="/admin/index" class="craft-cta">进入后台</RouterLink>
      </div>
    </div>
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

.craft-login {
  font-size: 0.925rem;
  color: #5a5a5a;
  text-decoration: none;
}

.craft-login:hover {
  color: #0f0f0f;
}

.craft-cta {
  display: inline-flex;
  padding: 0.45rem 0.9rem;
  border-radius: 6px;
  background: #0f0f0f;
  color: #fff;
  font-size: 0.875rem;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.15s ease;
}

.craft-cta:hover {
  background: #2a2a2a;
}

@media (max-width: 640px) {
  .craft-nav {
    gap: 0.85rem;
  }

  .craft-nav__link {
    font-size: 0.85rem;
  }

  .craft-cta {
    padding: 0.4rem 0.7rem;
    font-size: 0.8rem;
  }
}
</style>
