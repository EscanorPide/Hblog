<script setup>
import FrontendHeader from './components/FrontendHeader.vue'
import FrontendFooter from './components/FrontendFooter.vue'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { onMounted } from 'vue'

const blogSettingsStore = useBlogSettingsStore()

onMounted(() => {
  blogSettingsStore.fetchSettings()
})
</script>

<template>
  <div class="frontend-layout">
    <FrontendHeader />
    <main class="frontend-main">
      <router-view v-slot="{ Component, route }">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="route.fullPath" />
        </transition>
      </router-view>
    </main>
    <FrontendFooter />
  </div>
</template>

<style scoped>
.frontend-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  color: #0f0f0f;
  font-family: 'DM Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.frontend-main {
  flex: 1;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 2.5rem 1.5rem 4rem;
}

@media (min-width: 768px) {
  .frontend-main {
    padding: 3rem 2rem 5rem;
  }
}
</style>
