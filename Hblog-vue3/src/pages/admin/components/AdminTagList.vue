<template>
  <div class="tag-bar">
    <div ref="scrollRef" class="tag-scroll">
      <div
        v-for="tag in tags"
        :key="tag.path"
        class="route-tag"
        :class="{ active: isActive(tag) }"
        :data-path="tag.path"
      >
        <span class="tag-dot"></span>
        <button class="tag-link" type="button" @click="openTag(tag.path)">
          {{ tag.title }}
        </button>
        <button
          v-if="!tag.affix"
          class="close-button"
          type="button"
          aria-label="关闭标签"
          @click.stop="closeTag(tag.path)"
        >
          <el-icon><Close /></el-icon>
        </button>
      </div>
    </div>

    <el-dropdown trigger="click" @command="handleCommand">
      <button class="tag-more" type="button" aria-label="标签操作">
        <el-icon><MoreFilled /></el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="current" :disabled="isAffixPath(route.path)">
            关闭当前
          </el-dropdown-item>
          <el-dropdown-item command="others">关闭其他</el-dropdown-item>
          <el-dropdown-item command="all">关闭全部</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { Close, MoreFilled } from '@element-plus/icons-vue'
import { nextTick, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsView } from '@/composables/tagsView'

const route = useRoute()
const router = useRouter()
const scrollRef = ref(null)

const {
  HOME_PATH,
  homeTag,
  tags,
  addOrSyncTag,
  removeTag,
  keepOnly,
  resetToAffixed,
  isAffixPath,
} = useTagsView()

function isActive(tag) {
  return route.path === tag.path
}

function openTag(path) {
  if (route.path !== path) router.push(path)
}

function closeTag(path) {
  const result = removeTag(path)
  if (!result) return

  if (route.path === path) {
    const nextTag = tags[result.index] || tags[result.index - 1] || homeTag
    router.push(nextTag.path)
  }
}

function handleCommand(command) {
  if (command === 'current') {
    closeTag(route.path)
    return
  }

  if (command === 'others') {
    keepOnly([route.path])
    return
  }

  resetToAffixed()
  if (route.path !== HOME_PATH) router.push(HOME_PATH)
}

async function scrollActiveIntoView() {
  await nextTick()
  const root = scrollRef.value
  if (!root) return
  const active = root.querySelector('.route-tag.active')
  active?.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' })
}

watch(
  () => route.fullPath,
  () => {
    addOrSyncTag(route)
    scrollActiveIntoView()
  },
  { immediate: true },
)
</script>

<style scoped>
.tag-bar {
  height: 44px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  background: #fff;
  border-bottom: 1px solid #e9edf3;
}

.tag-scroll {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 8px;
  min-width: 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.tag-scroll::-webkit-scrollbar {
  display: none;
}

.route-tag {
  height: 29px;
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 7px;
  padding: 0 8px 0 10px;
  border: 1px solid #e5e7eb;
  border-radius: 7px;
  color: #6b7280;
  background: #fff;
  font-size: 12px;
  transition:
    border-color 0.2s,
    color 0.2s,
    background 0.2s;
}

.route-tag:hover {
  color: #4f46e5;
  border-color: #c7d2fe;
}

.tag-link {
  padding: 0;
  border: 0;
  color: inherit;
  background: transparent;
  font: inherit;
  cursor: pointer;
}

.route-tag.active {
  color: #4f46e5;
  border-color: #c7d2fe;
  background: #f5f5ff;
}

.tag-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d1d5db;
}

.active .tag-dot {
  background: #6366f1;
}

.close-button {
  width: 16px;
  height: 16px;
  display: grid;
  place-items: center;
  margin-left: 2px;
  padding: 0;
  border: 0;
  border-radius: 50%;
  color: inherit;
  background: transparent;
  cursor: pointer;
}

.close-button:hover,
.close-button:focus-visible {
  color: #fff;
  background: #9ca3af;
}

.tag-more {
  width: 34px;
  height: 30px;
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  margin-left: 10px;
  border: 0;
  border-left: 1px solid #e5e7eb;
  color: #9ca3af;
  background: #fff;
  cursor: pointer;
}

.tag-more:hover {
  color: #4f46e5;
}
</style>
