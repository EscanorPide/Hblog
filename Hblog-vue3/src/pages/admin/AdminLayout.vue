<template>
  <div class="admin-layout">
    <AdminMenu :collapsed="menuStore.menuCollapsed" />
    <button
      v-if="!menuStore.menuCollapsed"
      class="menu-overlay"
      type="button"
      aria-label="关闭侧边栏"
      @click="menuStore.setMenuCollapsed(true)"
    ></button>

    <div class="admin-main">
      <AdminHeader :collapsed="menuStore.menuCollapsed" @toggle-menu="menuStore.toggleMenu" />
      <AdminTagList />

      <main class="admin-content">
        <router-view v-slot="{ Component, route: viewRoute }">
          <transition name="fade-slide" mode="out-in">
            <keep-alive :include="cachedNames">
              <component :is="Component" :key="viewRoute.name || viewRoute.path" />
            </keep-alive>
          </transition>
        </router-view>
      </main>

      <AdminFooter />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useTagsView } from '@/composables/tagsView'
import { useMenuStore } from '@/stores/menu'
import { useUserStore } from '@/stores/user'
import AdminFooter from './components/AdminFooter.vue'
import AdminHeader from './components/AdminHeader.vue'
import AdminMenu from './components/AdminMenu.vue'
import AdminTagList from './components/AdminTagList.vue'

const menuStore = useMenuStore()
const { cachedNames } = useTagsView()
const userStore = useUserStore()

// 刷新页面后 Pinia 会丢失，进入后台布局时补拉用户信息
onMounted(() => {
  if (!userStore.userInfo?.username) {
    userStore.setUserInfo()
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
  color: #303442;
  background: #f5f7fb;
}

.admin-main {
  min-width: 0;
  display: flex;
  flex: 1;
  flex-direction: column;
}

.menu-overlay {
  display: none;
}

.admin-content {
  flex: 1;
  padding: 22px;
  overflow-y: auto;
}

@media (max-width: 800px) {
  .menu-overlay {
    position: fixed;
    z-index: 20;
    inset: 0;
    display: block;
    padding: 0;
    border: 0;
    background: rgba(15, 23, 42, 0.42);
    backdrop-filter: blur(2px);
    cursor: pointer;
  }
}

@media (max-width: 640px) {
  .admin-content {
    padding: 14px;
  }
}
</style>
