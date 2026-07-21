<script setup>
import { onMounted, reactive, ref } from 'vue'
import ArticleCard from './components/ArticleCard.vue'
import CraftAside from './components/CraftAside.vue'
import MinimalPager from './components/MinimalPager.vue'
import { getArticlePageList } from '@/api/frontend/article'

const articles = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 8,
  total: 0,
})

async function fetchArticles() {
  loading.value = true
  try {
    const res = await getArticlePageList({
      current: pagination.current,
      size: pagination.size,
    })
    if (res?.success === false) return
    articles.value = res.data || []
    pagination.total = Number(res.total || 0)
    pagination.current = Number(res.current || pagination.current)
    pagination.size = Number(res.size || pagination.size)
  } catch (err) {
    console.error('加载文章列表失败', err)
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  pagination.current = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(fetchArticles)
</script>

<template>
  <div class="craft-layout">
    <CraftAside />

    <section class="craft-main" v-loading="loading">
      <template v-if="articles.length">
        <div class="craft-grid">
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        </div>
        <div class="craft-pagination">
          <MinimalPager
            :current="pagination.current"
            :page-size="pagination.size"
            :total="pagination.total"
            @change="handlePageChange"
          />
        </div>
      </template>
      <div v-else-if="!loading" class="craft-empty">暂无文章，去后台发布第一篇吧。</div>
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

.craft-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 2.5rem 2rem;
}

.craft-pagination {
  display: flex;
  justify-content: center;
  padding-top: 3.5rem;
  padding-bottom: 0.5rem;
}

.craft-empty {
  padding: 4rem 1rem;
  text-align: center;
  color: #8a8a8a;
  font-size: 0.95rem;
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
