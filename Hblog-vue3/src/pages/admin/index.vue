<template>
  <div class="dashboard" v-loading="loading">
    <section class="welcome-card">
      <div>
        <p class="eyebrow">WELCOME BACK</p>
        <h1>{{ greeting }}，管理员</h1>
        <p>今天也要继续创作优质内容，保持博客活跃。</p>
      </div>
      <div class="welcome-date">
        <strong>{{ currentDay }}</strong>
        <span>{{ currentMonth }}</span>
      </div>
    </section>

    <section class="stat-grid" aria-label="数据概览">
      <article v-for="item in statistics" :key="item.label" class="stat-card">
        <div class="stat-icon" :class="item.theme">
          <el-icon :size="22"><component :is="item.icon" /></el-icon>
        </div>
        <div>
          <span>{{ item.label }}</span>
          <strong>
            <CountTo :value="item.value" :duration="1.8" />
          </strong>
          <small>{{ item.tip }}</small>
        </div>
      </article>
    </section>

    <section class="dashboard-grid">
      <article class="panel">
        <div class="panel-heading">
          <div>
            <h2>快捷操作</h2>
            <p>常用功能快速入口</p>
          </div>
        </div>
        <div class="quick-actions">
          <button
            v-for="action in quickActions"
            :key="action.label"
            type="button"
            @click="handleQuickAction(action)"
          >
            <el-icon :size="21"><component :is="action.icon" /></el-icon>
            <span>{{ action.label }}</span>
          </button>
        </div>
      </article>

      <article class="panel">
        <div class="panel-heading">
          <div>
            <h2>数据洞察</h2>
            <p>查看更完整的访问与发布统计</p>
          </div>
          <button class="text-button" type="button" @click="router.push('/admin/statistics')">
            进入数据统计
          </button>
        </div>
        <ul class="insight-list">
          <li>
            <span class="insight-label">文章总量</span>
            <strong>
              <CountTo :value="stats.articleTotalCount" :duration="1.8" />
            </strong>
          </li>
          <li>
            <span class="insight-label">分类 / 标签</span>
            <strong class="insight-pair">
              <CountTo :value="stats.categoryTotalCount" :duration="1.8" />
              <span>/</span>
              <CountTo :value="stats.tagTotalCount" :duration="1.8" />
            </strong>
          </li>
          <li>
            <span class="insight-label">累计浏览量</span>
            <strong>
              <CountTo :value="stats.pvTotalCount" :duration="2" />
            </strong>
          </li>
        </ul>
      </article>
    </section>
  </div>
</template>

<script setup>
import {
  CollectionTag,
  Document,
  EditPen,
  FolderOpened,
  Plus,
  Setting,
  View,
} from '@element-plus/icons-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getBaseStatisticsInfo } from '@/api/admin/dashboard'
import CountTo from '@/components/CountTo.vue'
import { showMessage } from '@/composables/util'

defineOptions({ name: 'AdminIndex' })

const router = useRouter()
const loading = ref(false)
const today = new Date()

const stats = reactive({
  articleTotalCount: 0,
  categoryTotalCount: 0,
  tagTotalCount: 0,
  pvTotalCount: 0,
})

const currentDay = computed(() => String(today.getDate()).padStart(2, '0'))
const currentMonth = computed(() =>
  today.toLocaleDateString('zh-CN', { month: 'short', weekday: 'short' }),
)

const greeting = computed(() => {
  const hour = today.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const statistics = computed(() => [
  {
    label: '文章总数',
    value: stats.articleTotalCount,
    tip: '已发布内容总量',
    icon: Document,
    theme: 'purple',
  },
  {
    label: '总浏览量',
    value: stats.pvTotalCount,
    tip: '全部文章阅读累计',
    icon: View,
    theme: 'blue',
  },
  {
    label: '分类数量',
    value: stats.categoryTotalCount,
    tip: '内容分类体系',
    icon: FolderOpened,
    theme: 'orange',
  },
  {
    label: '标签数量',
    value: stats.tagTotalCount,
    tip: '标签覆盖情况',
    icon: CollectionTag,
    theme: 'green',
  },
])

const quickActions = [
  { label: '发布文章', icon: EditPen, path: '/admin/articles' },
  { label: '新建分类', icon: Plus, path: '/admin/categories' },
  { label: '整理标签', icon: CollectionTag, path: '/admin/tags' },
  { label: '系统设置', icon: Setting, path: '/admin/settings' },
]

async function fetchStatistics() {
  loading.value = true
  try {
    const res = await getBaseStatisticsInfo()
    if (res?.success === false) return
    const data = res.data || {}
    stats.articleTotalCount = Number(data.articleTotalCount || 0)
    stats.categoryTotalCount = Number(data.categoryTotalCount || 0)
    stats.tagTotalCount = Number(data.tagTotalCount || 0)
    stats.pvTotalCount = Number(data.pvTotalCount || 0)
  } catch (err) {
    console.error('获取仪表盘统计失败', err)
    showMessage('获取首页统计失败', 'error')
  } finally {
    loading.value = false
  }
}

function handleQuickAction(action) {
  if (action.path) {
    router.push(action.path)
  }
}

onMounted(fetchStatistics)
</script>

<style scoped>
.welcome-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26px 30px;
  border: 1px solid rgba(99, 102, 241, 0.12);
  border-radius: 16px;
  background:
    radial-gradient(circle at 82% 15%, rgba(139, 92, 246, 0.18), transparent 28%),
    linear-gradient(120deg, #fff, #f6f5ff);
  box-shadow: 0 8px 30px rgba(30, 41, 59, 0.04);
}

.eyebrow {
  margin: 0 0 5px;
  color: #6366f1;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.17em;
}

.welcome-card h1 {
  margin: 0;
  color: #272a36;
  font-size: 24px;
  line-height: 1.4;
}

.welcome-card p:last-child {
  margin: 6px 0 0;
  color: #8b91a1;
  font-size: 13px;
}

.welcome-date {
  width: 92px;
  height: 76px;
  display: flex;
  flex: 0 0 auto;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  color: #fff;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 12px 28px rgba(99, 102, 241, 0.25);
}

.welcome-date strong {
  font-size: 27px;
  line-height: 1.1;
}

.welcome-date span {
  margin-top: 5px;
  font-size: 11px;
  opacity: 0.8;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border: 1px solid #eaedf2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 6px 22px rgba(30, 41, 59, 0.035);
}

.stat-card > div:last-child {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.stat-icon {
  width: 46px;
  height: 46px;
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 13px;
}

.purple {
  color: #fff;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
}

.blue {
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
}

.orange {
  color: #fff;
  background: linear-gradient(135deg, #ea580c, #f59e0b);
}

.green {
  color: #fff;
  background: linear-gradient(135deg, #059669, #10b981);
}

.stat-card span {
  color: #5b6472;
  font-size: 13px;
  font-weight: 600;
}

.stat-card strong {
  display: block;
  margin: 4px 0 2px;
  color: #111827;
  font-size: 34px;
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -0.03em;
}

.stat-card strong :deep(.count-to) {
  font-size: inherit;
  font-weight: inherit;
  letter-spacing: inherit;
  line-height: inherit;
}

.stat-card small {
  overflow: hidden;
  color: #6b7280;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.2fr);
  gap: 18px;
  margin-top: 18px;
}

.panel {
  padding: 22px;
  border: 1px solid #eaedf2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 6px 22px rgba(30, 41, 59, 0.035);
}

.panel-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.panel-heading h2 {
  margin: 0;
  color: #303442;
  font-size: 15px;
}

.panel-heading p {
  margin: 3px 0 0;
  color: #a3a8b5;
  font-size: 11px;
}

.text-button {
  border: 0;
  color: #6366f1;
  background: transparent;
  font-size: 12px;
  cursor: pointer;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.quick-actions button {
  min-height: 82px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid #edf0f4;
  border-radius: 12px;
  color: #697084;
  background: #fafbfc;
  font-size: 11px;
  cursor: pointer;
  transition:
    color 0.2s,
    border-color 0.2s,
    transform 0.2s;
}

.quick-actions button:hover {
  color: #6366f1;
  border-color: #c7d2fe;
  transform: translateY(-2px);
}

.insight-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.insight-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #fafbfc;
  border: 1px solid #edf0f4;
}

.insight-label {
  color: #8b91a1;
  font-size: 13px;
}

.insight-list strong {
  color: #1f2430;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.insight-list strong :deep(.count-to) {
  font-size: inherit;
  font-weight: inherit;
}

.insight-pair {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 1180px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .welcome-card {
    padding: 21px;
  }

  .welcome-card h1 {
    font-size: 20px;
  }

  .welcome-date {
    display: none;
  }

  .stat-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
