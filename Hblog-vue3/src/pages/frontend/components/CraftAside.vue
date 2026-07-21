<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import moment from 'moment'
import { useBlogSettingsStore } from '@/stores/blogSettings'
import { getCategoryList } from '@/api/frontend/category'
import { getTagList } from '@/api/frontend/tag'
import { getArticlePageList } from '@/api/frontend/article'
import { Folder } from '@element-plus/icons-vue'

const { settings } = storeToRefs(useBlogSettingsStore())

const categories = ref([])
const tags = ref([])
const hotArticles = ref([])
const loading = ref(false)

const CATEGORY_LIMIT = 8
const TAG_LIMIT = 12

const blogName = computed(() => settings.value.name || 'Hblog')
const intro = computed(
  () => settings.value.introduction || '分享技术与思考，记录构建过程中的想法。',
)

const titleParts = computed(() => {
  const name = blogName.value.trim()
  const parts = name.split(/\s+|&|·|｜/).filter(Boolean)
  if (parts.length >= 2) {
    return { left: parts[0], right: parts.slice(1).join(' '), split: true }
  }
  return { left: name, right: '', split: false }
})

const visibleCategories = computed(() => categories.value.slice(0, CATEGORY_LIMIT))
const visibleTags = computed(() => tags.value.slice(0, TAG_LIMIT))

function formatDate(value) {
  if (!value) return ''
  return moment(value).format('MM-DD')
}

onMounted(async () => {
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
      hotArticles.value = hotRes.data || []
    }
  } catch (err) {
    console.error('加载左侧栏失败', err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <aside class="craft-aside" v-loading="loading">
    <div class="craft-aside__head">
      <h1 class="craft-aside__title">
        <template v-if="titleParts.split">
          <span>{{ titleParts.left }}</span>
          <span class="craft-aside__amp">&amp;</span>
          <span>{{ titleParts.right }}</span>
        </template>
        <template v-else>{{ titleParts.left }}</template>
      </h1>
      <p class="craft-aside__desc">{{ intro }}</p>
    </div>

    <div class="craft-aside__cards">
      <!-- 热门文章 -->
      <div class="aside-card">
        <h3 class="aside-card__title">热门文章</h3>
        <ul v-if="hotArticles.length" class="aside-card__hot">
          <li v-for="(item, index) in hotArticles" :key="item.id">
            <RouterLink :to="`/article/${item.id}`" class="aside-card__hot-link">
              <span class="aside-card__rank" :class="{ 'is-top': index < 3 }">
                {{ index + 1 }}
              </span>
              <span class="aside-card__hot-main">
                <span class="aside-card__hot-title">{{ item.title }}</span>
                <span class="aside-card__hot-date">{{ formatDate(item.createTime) }}</span>
              </span>
            </RouterLink>
          </li>
        </ul>
        <p v-else class="aside-card__empty">暂无文章</p>
      </div>

      <!-- 分类 -->
      <div class="aside-card">
        <div class="aside-card__head">
          <h3 class="aside-card__title">分类</h3>
          <RouterLink to="/category" class="aside-card__more">全部</RouterLink>
        </div>
        <ul v-if="visibleCategories.length" class="aside-card__list">
          <li v-for="item in visibleCategories" :key="item.id">
            <RouterLink :to="`/category/${item.id}`" class="aside-card__list-link">
              <span class="aside-card__list-name">
                <el-icon class="aside-card__folder"><Folder /></el-icon>
                <span class="truncate">{{ item.name }}</span>
              </span>
              <span class="aside-card__count">{{ item.articlesTotal ?? 0 }}</span>
            </RouterLink>
          </li>
        </ul>
        <p v-else class="aside-card__empty">暂无分类</p>
      </div>

      <!-- 标签 -->
      <div class="aside-card">
        <div class="aside-card__head">
          <h3 class="aside-card__title">标签</h3>
          <RouterLink to="/tag" class="aside-card__more">全部</RouterLink>
        </div>
        <div v-if="visibleTags.length" class="aside-card__tags">
          <RouterLink
            v-for="item in visibleTags"
            :key="item.id"
            :to="`/tag/${item.id}`"
            class="aside-card__tag"
            :title="item.name"
          >
            {{ item.name }}
          </RouterLink>
        </div>
        <p v-else class="aside-card__empty">暂无标签</p>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.craft-aside {
  position: sticky;
  top: 5.5rem;
}

.craft-aside__head {
  margin-bottom: 1.5rem;
}

.craft-aside__title {
  font-family: 'Source Serif 4', Georgia, 'Songti SC', serif;
  font-size: clamp(1.85rem, 2.8vw, 2.5rem);
  font-weight: 600;
  line-height: 1.15;
  letter-spacing: -0.03em;
  color: #0f0f0f;
  margin: 0;
}

.craft-aside__amp {
  display: inline-block;
  margin: 0 0.15em;
  font-style: italic;
  font-weight: 500;
}

.craft-aside__desc {
  margin: 0.85rem 0 0;
  font-size: 0.9rem;
  line-height: 1.55;
  color: #6b6b6b;
  max-width: 18rem;
}

.craft-aside__cards {
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.aside-card {
  background: #fafafa;
  border: 1px solid #efefef;
  border-radius: 12px;
  padding: 0.95rem 1rem;
}

.aside-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.aside-card__head .aside-card__title {
  margin-bottom: 0;
}

.aside-card__title {
  margin: 0 0 0.75rem;
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f0f0f;
}

.aside-card__more {
  font-size: 0.75rem;
  color: #8a8a8a;
  text-decoration: none;
  flex-shrink: 0;
}

.aside-card__more:hover {
  color: #0f0f0f;
  text-decoration: underline;
}

.aside-card__hot {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.aside-card__hot-link {
  display: flex;
  gap: 0.55rem;
  align-items: flex-start;
  text-decoration: none;
  color: #0f0f0f;
}

.aside-card__hot-link:hover .aside-card__hot-title {
  color: #0f0f0f;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.aside-card__rank {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  font-size: 0.7rem;
  line-height: 18px;
  text-align: center;
  flex-shrink: 0;
  background: #efefef;
  color: #6b6b6b;
  font-weight: 600;
}

.aside-card__rank.is-top {
  background: #0f0f0f;
  color: #fff;
}

.aside-card__hot-main {
  min-width: 0;
  flex: 1;
}

.aside-card__hot-title {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 0.82rem;
  line-height: 1.4;
  font-weight: 500;
}

.aside-card__hot-date {
  display: block;
  margin-top: 0.15rem;
  font-size: 0.7rem;
  color: #9a9a9a;
}

.aside-card__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.aside-card__list-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #3a3a3a;
  text-decoration: none;
  padding: 0.15rem 0;
}

.aside-card__list-link:hover {
  color: #2383e2;
}

.aside-card__list-name {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  min-width: 0;
}

.aside-card__folder {
  font-size: 0.95rem;
  color: #8a8a8a;
  flex-shrink: 0;
}

.aside-card__list-link:hover .aside-card__folder {
  color: #2383e2;
}

.aside-card__count {
  font-size: 0.75rem;
  color: #9a9a9a;
  flex-shrink: 0;
}

.aside-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.aside-card__tag {
  display: inline-block;
  max-width: 7.5rem;
  padding: 0.2rem 0.55rem;
  font-size: 0.75rem;
  line-height: 1.4;
  color: #3a3a3a;
  background: #f3f3f3;
  border-radius: 0.25rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-decoration: none;
}

.aside-card__tag:hover {
  color: #0f0f0f;
  background: #e8e8e8;
}

.aside-card__empty {
  margin: 0;
  font-size: 0.8rem;
  color: #9a9a9a;
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
