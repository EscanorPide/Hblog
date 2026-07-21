<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar, Folder, Opportunity, View } from '@element-plus/icons-vue'
import moment from 'moment'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'
import { getArticleDetail } from '@/api/frontend/article'
import CraftAside from './components/CraftAside.vue'
import CommentSection from './components/CommentSection.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const article = ref(null)

/** 评论归属路由，与后端 routerUrl 约定一致 */
const commentRouterUrl = computed(() => {
  const id = route.params.id
  return id ? `/article/${id}` : ''
})

const dateText = computed(() => {
  if (!article.value?.createTime) return ''
  return moment(article.value.createTime).format('YYYY-MM-DD HH:mm:ss')
})

const categoryName = computed(
  () => article.value?.categoryName || article.value?.category || '',
)

const readCount = computed(() => {
  const raw =
    article.value?.readNum ??
    article.value?.readCount ??
    article.value?.viewCount ??
    article.value?.pv ??
    0
  return Number(raw) || 0
})

const articleTags = computed(() => {
  const list = article.value?.tags || article.value?.tagList || []
  if (!Array.isArray(list)) return []
  return list
    .map((item) => {
      if (typeof item === 'string') return { id: item, name: item }
      return { id: item.id ?? item.tagId, name: item.name || item.tagName || '' }
    })
    .filter((item) => item.name)
})

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) {
    router.replace('/')
    return
  }

  loading.value = true
  article.value = null
  try {
    const res = await getArticleDetail({ id })
    if (res?.success === false) {
      router.replace('/')
      return
    }
    article.value = res.data || null
  } catch (err) {
    console.error('加载文章详情失败', err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchDetail)
watch(() => route.params.id, fetchDetail)
</script>

<template>
  <div class="craft-layout">
    <CraftAside />

    <section class="craft-main" v-loading="loading">
      <template v-if="article">
        <!-- 面包屑 -->
        <nav class="detail-breadcrumb" aria-label="面包屑">
          <RouterLink to="/">首页</RouterLink>
          <span class="detail-breadcrumb__sep">/</span>
          <span class="detail-breadcrumb__current">正文</span>
        </nav>

        <!-- 标题 -->
        <h1 class="detail-title">{{ article.title }}</h1>

        <!-- 元信息：发表时间 / 分类 / 阅读量 -->
        <div class="detail-meta">
          <span v-if="dateText" class="detail-meta__item">
            <el-icon :size="14"><Calendar /></el-icon>
            发表于 {{ dateText }}
          </span>
          <span v-if="categoryName" class="detail-meta__item">
            <el-icon :size="14"><Folder /></el-icon>
            分类于
            <RouterLink
              v-if="article.categoryId"
              :to="`/category/${article.categoryId}`"
              class="detail-meta__link"
            >
              {{ categoryName }}
            </RouterLink>
            <template v-else>{{ categoryName }}</template>
          </span>
          <span class="detail-meta__item">
            <el-icon :size="14"><View /></el-icon>
            阅读量 {{ readCount }}
          </span>
        </div>

        <!-- 摘要 callout -->
        <aside v-if="article.summary" class="detail-summary" aria-label="文章摘要">
          <div class="detail-summary__icon" aria-hidden="true">
            <el-icon :size="28"><Opportunity /></el-icon>
          </div>
          <div class="detail-summary__body">
            <p class="detail-summary__label">摘要</p>
            <p class="detail-summary__text">{{ article.summary }}</p>
          </div>
        </aside>

        <!-- 正文 -->
        <div class="detail-content">
          <MdPreview :model-value="article.content || ''" editor-id="article-preview" />
        </div>

        <!-- 文章标签 -->
        <div v-if="articleTags.length" class="detail-tags">
          <span class="detail-tags__label">标签</span>
          <RouterLink
            v-for="tag in articleTags"
            :key="tag.id"
            :to="`/tag/${tag.id}`"
            class="detail-tags__chip"
          >
            {{ tag.name }}
          </RouterLink>
        </div>

        <!-- 评论区 -->
        <CommentSection v-if="commentRouterUrl" :router-url="commentRouterUrl" />
      </template>

      <div v-else-if="!loading" class="detail-empty">文章不存在或已删除</div>
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

.detail-breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.45rem;
  margin-bottom: 1.25rem;
  font-size: 0.875rem;
  color: #8a8a8a;
}

.detail-breadcrumb a {
  color: #6b6b6b;
  text-decoration: none;
}

.detail-breadcrumb a:hover {
  color: #0f0f0f;
}

.detail-breadcrumb__sep {
  color: #c4c4c4;
}

.detail-breadcrumb__current {
  color: #0f0f0f;
}

.detail-title {
  margin: 0;
  font-family: 'Source Serif 4', Georgia, 'Songti SC', serif;
  font-size: clamp(1.75rem, 3.6vw, 2.35rem);
  font-weight: 700;
  line-height: 1.3;
  letter-spacing: -0.02em;
  color: #0f0f0f;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.35rem 1.15rem;
  margin-top: 1rem;
  font-size: 0.85rem;
  color: #8a8a8a;
}

.detail-meta__item {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
}

.detail-meta__item .el-icon {
  color: #a3a3a3;
}

.detail-meta__link {
  color: #6b6b6b;
  text-decoration: none;
}

.detail-meta__link:hover {
  color: #0f0f0f;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.detail-summary {
  display: flex;
  gap: 1rem;
  margin: 1.75rem 0 0;
  padding: 1.25rem 1.35rem;
  border-radius: 14px;
  background: #f4f6f8;
  border: 1px solid #e8ebef;
}

.detail-summary__icon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  margin-top: 0.1rem;
  color: #6b7280;
}

.detail-summary__body {
  min-width: 0;
  flex: 1;
}

.detail-summary__label {
  margin: 0 0 0.45rem;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: #0f0f0f;
}

.detail-summary__text {
  margin: 0;
  font-size: 0.95rem;
  line-height: 1.75;
  color: #3a3a3a;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-content {
  margin-top: 1.75rem;
  padding-top: 0.25rem;
}

/* md-editor 预览区贴近当前黑白风格 */
.detail-content :deep(.md-editor-preview-wrapper) {
  padding: 0;
}

.detail-content :deep(.md-editor-preview) {
  font-size: 1.02rem;
  line-height: 1.85;
  color: #2a2a2a;
}

.detail-content :deep(.md-editor-preview h1),
.detail-content :deep(.md-editor-preview h2),
.detail-content :deep(.md-editor-preview h3),
.detail-content :deep(.md-editor-preview h4) {
  color: #0f0f0f;
  font-weight: 700;
  margin-top: 1.75em;
  margin-bottom: 0.65em;
}

.detail-content :deep(.md-editor-preview p) {
  margin: 0.9em 0;
}

.detail-content :deep(.md-editor-preview a) {
  color: #0f0f0f;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.detail-content :deep(.md-editor-preview blockquote) {
  margin: 1.25em 0;
  padding: 0.9rem 1.1rem;
  border-left: 3px solid #d4d4d4;
  border-radius: 0 8px 8px 0;
  background: #f7f7f7;
  color: #4a4a4a;
}

.detail-content :deep(.md-editor-preview code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
  font-size: 0.9em;
}

.detail-content :deep(.md-editor-preview pre) {
  border-radius: 10px;
  background: #f4f4f4 !important;
}

/* 代码块头部默认 sticky + z-index:10000，整页滚动时会吸顶盖住导航栏，这里取消吸顶 */
.detail-content :deep(.md-editor-preview .md-editor-code .md-editor-code-head) {
  position: static;
  z-index: auto;
}

.detail-content :deep(.md-editor-preview img) {
  border-radius: 10px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #efefef;
}

.detail-tags__label {
  margin-right: 0.25rem;
  font-size: 0.8rem;
  color: #8a8a8a;
}

.detail-tags__chip {
  display: inline-block;
  padding: 0.22rem 0.65rem;
  border-radius: 0.3rem;
  background: #f3f3f3;
  color: #3a3a3a;
  font-size: 0.78rem;
  text-decoration: none;
}

.detail-tags__chip:hover {
  background: #e8e8e8;
  color: #0f0f0f;
}

.detail-empty {
  padding: 4rem 1rem;
  text-align: center;
  color: #8a8a8a;
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
}
</style>
