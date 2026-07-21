<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from './components/ArticleCard.vue'
import CraftAside from './components/CraftAside.vue'
import MinimalPager from './components/MinimalPager.vue'
import { getCategoryArticlePageList } from '@/api/frontend/article'
import { getCategoryList } from '@/api/frontend/category'
import { Folder } from '@element-plus/icons-vue'

const route = useRoute()
const articles = ref([])
const categories = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 8,
  total: 0,
})

const categoryId = computed(() => {
  const id = Number(route.params.id)
  return Number.isFinite(id) && id > 0 ? id : null
})

const currentCategory = computed(() => {
  if (!categoryId.value) return null
  return categories.value.find((item) => item.id === categoryId.value) || null
})

async function fetchCategories() {
  try {
    const res = await getCategoryList()
    if (res?.success !== false) {
      categories.value = res.data || []
    }
  } catch (err) {
    console.error('加载分类失败', err)
  }
}

async function fetchArticles() {
  if (!categoryId.value) {
    articles.value = []
    pagination.total = 0
    return
  }

  loading.value = true
  try {
    const res = await getCategoryArticlePageList({
      categoryId: categoryId.value,
      current: pagination.current,
      size: pagination.size,
    })
    if (res?.success === false) return
    articles.value = res.data || []
    pagination.total = Number(res.total || 0)
  } catch (err) {
    console.error('加载分类文章失败', err)
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
    fetchArticles()
  },
)

onMounted(async () => {
  await fetchCategories()
  await fetchArticles()
})
</script>

<template>
  <div class="craft-layout">
    <CraftAside />

    <section class="craft-main" v-loading="loading">
      <template v-if="!categoryId">
        <h2 class="craft-section-title">全部分类</h2>
        <ul v-if="categories.length" class="category-index">
          <li v-for="item in categories" :key="item.id">
            <RouterLink :to="`/category/${item.id}`" class="category-index__link">
              <span class="category-index__name">
                <el-icon class="category-index__icon"><Folder /></el-icon>
                {{ item.name }}
              </span>
              <span class="category-index__count">{{ item.articlesTotal ?? 0 }}</span>
            </RouterLink>
          </li>
        </ul>
        <div v-else class="craft-empty">暂无分类</div>
      </template>

      <template v-else>
        <h2 class="craft-section-title">{{ currentCategory?.name || '分类文章' }}</h2>
        <div v-if="articles.length" class="craft-grid">
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        </div>
        <div v-else-if="!loading" class="craft-empty">该分类下暂无文章</div>
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

.craft-section-title {
  margin: 0 0 1.75rem;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #0f0f0f;
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

.category-index {
  list-style: none;
  margin: 0;
  padding: 0;
  border-top: 1px solid #efefef;
}

.category-index__link {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #efefef;
  color: #0f0f0f;
  text-decoration: none;
  font-size: 1.05rem;
  font-weight: 500;
}

.category-index__link:hover {
  color: #2383e2;
}

.category-index__name {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  min-width: 0;
}

.category-index__icon {
  font-size: 1.1rem;
  color: #8a8a8a;
  flex-shrink: 0;
}

.category-index__link:hover .category-index__icon {
  color: #2383e2;
}

.category-index__count {
  font-size: 0.875rem;
  color: #8a8a8a;
  font-weight: 400;
}

@media (min-width: 640px) {
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
}
</style>
