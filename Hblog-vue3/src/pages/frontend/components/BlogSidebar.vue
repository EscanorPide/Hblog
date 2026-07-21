<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import moment from 'moment'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { getCategoryList } from '@/api/frontend/category'
import { getTagList } from '@/api/frontend/tag'
import { getArticlePageList } from '@/api/frontend/article'

const props = defineProps({
  hotArticles: {
    type: Array,
    default: () => [],
  },
})

const { settings } = storeToRefs(useBlogSettingsStore())

const categories = ref([])
const tags = ref([])
const localHotArticles = ref([])
const loading = ref(false)

const CATEGORY_LIMIT = 6
const TAG_LIMIT = 8

const socialLinks = computed(() => {
  const list = []
  if (settings.value.githubHomepage) {
    list.push({ label: 'GitHub', href: settings.value.githubHomepage })
  }
  if (settings.value.giteeHomepage) {
    list.push({ label: 'Gitee', href: settings.value.giteeHomepage })
  }
  if (settings.value.zhihuHomepage) {
    list.push({ label: '知乎', href: settings.value.zhihuHomepage })
  }
  if (settings.value.csdnHomepage) {
    list.push({ label: 'CSDN', href: settings.value.csdnHomepage })
  }
  return list
})

const visibleCategories = computed(() => categories.value.slice(0, CATEGORY_LIMIT))
const visibleTags = computed(() => tags.value.slice(0, TAG_LIMIT))
const hotList = computed(() => {
  const fromProps = props.hotArticles || []
  if (fromProps.length) return fromProps.slice(0, 5)
  return localHotArticles.value.slice(0, 5)
})

function formatDate(value) {
  if (!value) return ''
  return moment(value).format('MM-DD')
}

async function fetchSidebar() {
  loading.value = true
  try {
    const [categoryRes, tagRes, hotRes] = await Promise.all([
      getCategoryList(),
      getTagList(),
      getArticlePageList({ current: 1, size: 5 }),
    ])
    if (categoryRes?.success !== false) {
      categories.value = categoryRes.data || []
    }
    if (tagRes?.success !== false) {
      tags.value = tagRes.data || []
    }
    if (hotRes?.success !== false) {
      localHotArticles.value = hotRes.data || []
    }
  } catch (err) {
    console.error('加载侧栏失败', err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchSidebar)
</script>

<template>
  <aside class="sidebar space-y-4" v-loading="loading">
    <!-- 关于我 -->
    <div class="sidebar-card">
      <h3 class="sidebar-title">关于我</h3>
      <div class="flex items-start gap-3">
        <img
          v-if="settings.avatar"
          :src="settings.avatar"
          alt="作者头像"
          class="w-11 h-11 rounded-full object-cover border border-gray-100 shrink-0"
        />
        <div
          v-else
          class="w-11 h-11 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-semibold shrink-0"
        >
          {{ (settings.author || '博').slice(0, 1) }}
        </div>
        <div class="min-w-0 flex-1">
          <p class="font-semibold text-gray-900 text-sm">{{ settings.author || '博主' }}</p>
          <p class="text-xs text-gray-500 mt-1 leading-relaxed line-clamp-3">
            {{ settings.introduction || '这个人很懒，还没有写介绍。' }}
          </p>
        </div>
      </div>
      <div v-if="socialLinks.length" class="flex flex-wrap gap-x-3 gap-y-1 mt-3 text-xs">
        <a
          v-for="item in socialLinks"
          :key="item.label"
          :href="item.href"
          target="_blank"
          rel="noopener noreferrer"
          class="text-blue-600 hover:underline"
        >
          {{ item.label }}
        </a>
      </div>
    </div>

    <!-- 热门文章 -->
    <div class="sidebar-card">
      <h3 class="sidebar-title">热门文章</h3>
      <ul v-if="hotList.length" class="space-y-2.5">
        <li v-for="(item, index) in hotList" :key="item.id">
          <RouterLink
            :to="`/article/${item.id}`"
            class="group flex gap-2.5 items-start text-sm text-gray-700 hover:text-blue-700"
          >
            <span
              class="shrink-0 w-5 h-5 rounded text-[11px] leading-5 text-center font-medium"
              :class="index < 3 ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-500'"
            >
              {{ index + 1 }}
            </span>
            <span class="min-w-0 flex-1">
              <span class="line-clamp-2 leading-snug group-hover:underline">{{ item.title }}</span>
              <span class="block text-[11px] text-gray-400 mt-0.5">{{ formatDate(item.createTime) }}</span>
            </span>
          </RouterLink>
        </li>
      </ul>
      <p v-else class="text-xs text-gray-400">暂无文章</p>
    </div>

    <!-- 分类 -->
    <div class="sidebar-card sidebar-card--compact">
      <div class="flex items-center justify-between mb-2.5">
        <h3 class="sidebar-title !mb-0">分类</h3>
        <RouterLink to="/category" class="text-xs text-blue-600 hover:underline">全部</RouterLink>
      </div>
      <ul v-if="visibleCategories.length" class="space-y-1.5 text-sm">
        <li v-for="item in visibleCategories" :key="item.id">
          <RouterLink
            :to="`/category/${item.id}`"
            class="flex items-center justify-between gap-2 text-gray-600 hover:text-blue-700 py-0.5"
          >
            <span class="truncate">{{ item.name }}</span>
            <span class="text-xs text-gray-400 shrink-0">{{ item.articlesTotal ?? 0 }}</span>
          </RouterLink>
        </li>
      </ul>
      <p v-else class="text-xs text-gray-400">暂无分类</p>
    </div>

    <!-- 标签 -->
    <div class="sidebar-card sidebar-card--compact">
      <div class="flex items-center justify-between mb-2.5">
        <h3 class="sidebar-title !mb-0">标签</h3>
        <RouterLink to="/tag" class="text-xs text-blue-600 hover:underline">全部</RouterLink>
      </div>
      <div v-if="visibleTags.length" class="flex flex-wrap gap-1.5">
        <RouterLink
          v-for="item in visibleTags"
          :key="item.id"
          :to="`/tag/${item.id}`"
          class="tag-chip"
          :title="item.name"
        >
          {{ item.name }}
        </RouterLink>
      </div>
      <p v-else class="text-xs text-gray-400">暂无标签</p>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 100%;
}

.sidebar-card {
  background: #fff;
  border: 1px solid #f3f4f6;
  border-radius: 0.5rem;
  padding: 1rem;
}

.sidebar-card--compact {
  padding: 0.875rem 1rem;
}

.sidebar-title {
  margin-bottom: 0.75rem;
  font-size: 0.9375rem;
  font-weight: 700;
  color: #111827;
}

.tag-chip {
  display: inline-block;
  max-width: 100%;
  padding: 0.2rem 0.55rem;
  font-size: 0.75rem;
  line-height: 1.4;
  color: #1d4ed8;
  background: #eff6ff;
  border-radius: 0.25rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 7.5rem;
}

.tag-chip:hover {
  background: #dbeafe;
}
</style>
