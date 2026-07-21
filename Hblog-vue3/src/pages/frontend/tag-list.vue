<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import ArticleCard from './components/ArticleCard.vue'
import CraftAside from './components/CraftAside.vue'
import MinimalPager from './components/MinimalPager.vue'
import { getTagArticlePageList } from '@/api/frontend/article'
import { getTagList } from '@/api/frontend/tag'

const route = useRoute()
const articles = ref([])
const tags = ref([])
const loading = ref(false)
const keyword = ref('')
const pagination = reactive({
  current: 1,
  size: 8,
  total: 0,
})

const tagId = computed(() => {
  const id = Number(route.params.id)
  return Number.isFinite(id) && id > 0 ? id : null
})

const currentTag = computed(() => {
  if (!tagId.value) return null
  return tags.value.find((item) => item.id === tagId.value) || null
})

const filteredTags = computed(() => {
  const q = keyword.value.trim().toLowerCase()
  if (!q) return tags.value
  return tags.value.filter((item) => {
    const name = String(item.name || '').toLowerCase()
    const desc = String(getTagDesc(item) || '').toLowerCase()
    return name.includes(q) || desc.includes(q)
  })
})

function getTagCount(item) {
  return Number(item.articlesTotal ?? item.articleCount ?? item.count ?? 0) || 0
}

function getTagDesc(item) {
  return item.description || item.intro || item.remark || `${item.name || '标签'}相关内容`
}

async function fetchTags() {
  try {
    const res = await getTagList()
    if (res?.success !== false) {
      tags.value = res.data || []
    }
  } catch (err) {
    console.error('加载标签失败', err)
  }
}

async function fetchArticles() {
  if (!tagId.value) {
    articles.value = []
    pagination.total = 0
    return
  }

  loading.value = true
  try {
    const res = await getTagArticlePageList({
      tagId: tagId.value,
      current: pagination.current,
      size: pagination.size,
    })
    if (res?.success === false) return
    articles.value = res.data || []
    pagination.total = Number(res.total || 0)
  } catch (err) {
    console.error('加载标签文章失败', err)
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  pagination.current = page
  fetchArticles()
}

watch(
  () => route.params.id,
  () => {
    pagination.current = 1
    keyword.value = ''
    fetchArticles()
  },
)

onMounted(async () => {
  await fetchTags()
  await fetchArticles()
})
</script>

<template>
  <div class="craft-layout">
    <CraftAside />

    <section class="craft-main" v-loading="loading">
      <template v-if="!tagId">
        <div class="tag-page-head">
          <h2 class="craft-section-title">全部标签</h2>
          <label class="tag-search">
            <el-icon class="tag-search__icon"><Search /></el-icon>
            <input
              v-model="keyword"
              type="search"
              class="tag-search__input"
              placeholder="搜索标签"
              autocomplete="off"
            />
          </label>
        </div>

        <div v-if="filteredTags.length" class="tag-card-grid">
          <RouterLink
            v-for="item in filteredTags"
            :key="item.id"
            :to="`/tag/${item.id}`"
            class="tag-card"
          >
            <h3 class="tag-card__name"># {{ item.name }}</h3>
            <p class="tag-card__count">{{ getTagCount(item) }} 篇文章</p>
            <p class="tag-card__desc">{{ getTagDesc(item) }}</p>
          </RouterLink>
        </div>
        <div v-else class="craft-empty">
          {{ keyword.trim() ? '未找到匹配的标签' : '暂无标签' }}
        </div>
      </template>

      <template v-else>
        <h2 class="craft-section-title"># {{ currentTag?.name || '标签文章' }}</h2>
        <p v-if="currentTag" class="tag-detail-meta">
          {{ getTagCount(currentTag) }} 篇文章
          <template v-if="getTagDesc(currentTag)"> · {{ getTagDesc(currentTag) }}</template>
        </p>
        <div v-if="articles.length" class="craft-grid">
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        </div>
        <div v-else-if="!loading" class="craft-empty">该标签下暂无文章</div>
        <div v-if="articles.length" class="craft-pagination">
          <MinimalPager
            :current="pagination.current"
            :page-size="pagination.size"
            :total="pagination.total"
            @change="handlePageChange"
          />
        </div>
      </template>
    </section>
  </div>
</template>

<style scoped>
.craft-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 2.5rem;
  align-items: start;
}

.craft-main {
  min-width: 0;
}

.tag-page-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 1rem 1.5rem;
  margin-bottom: 1.5rem;
}

.craft-section-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #0f0f0f;
}

.tag-search {
  display: flex;
  align-items: center;
  gap: 0.45rem;
  min-width: min(100%, 240px);
  height: 40px;
  padding: 0 0.85rem;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  background: #fafafa;
  transition:
    border-color 0.15s ease,
    background 0.15s ease;
}

.tag-search:focus-within {
  border-color: #cfcfcf;
  background: #fff;
}

.tag-search__icon {
  color: #9a9a9a;
  flex-shrink: 0;
}

.tag-search__input {
  width: 100%;
  border: 0;
  outline: none;
  background: transparent;
  font-size: 0.9rem;
  color: #0f0f0f;
}

.tag-search__input::placeholder {
  color: #a3a3a3;
}

.tag-card-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
}

.tag-card {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  padding: 1.25rem 1.3rem;
  border: 1px solid #ececec;
  border-radius: 14px;
  background: #fff;
  text-decoration: none;
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease,
    transform 0.15s ease;
}

.tag-card:hover {
  border-color: #d8d8d8;
  box-shadow: 0 8px 24px rgba(15, 15, 15, 0.05);
  transform: translateY(-2px);
}

.tag-card__name {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 700;
  color: #0f0f0f;
  letter-spacing: -0.01em;
}

.tag-card__count {
  margin: 0;
  font-size: 0.85rem;
  color: #8a8a8a;
}

.tag-card__desc {
  margin: 0.2rem 0 0;
  font-size: 0.875rem;
  line-height: 1.55;
  color: #5a5a5a;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tag-detail-meta {
  margin: -0.75rem 0 1.5rem;
  font-size: 0.9rem;
  color: #8a8a8a;
}

.craft-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 2.5rem 2rem;
}

.craft-pagination {
  display: flex;
  justify-content: center;
  padding-top: 3rem;
}

.craft-empty {
  padding: 3rem 1rem;
  text-align: center;
  color: #8a8a8a;
}

@media (min-width: 640px) {
  .tag-card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .craft-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 1024px) {
  .craft-layout {
    grid-template-columns: 280px minmax(0, 1fr);
    gap: 4rem;
  }
}

@media (min-width: 1200px) {
  .craft-layout {
    grid-template-columns: 300px minmax(0, 1fr);
    gap: 5rem;
  }

  .tag-card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1.15rem;
  }
}
</style>
