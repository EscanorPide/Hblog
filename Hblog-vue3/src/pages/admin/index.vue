<template>
  <div class="dashboard">
    <section class="welcome-card">
      <div>
        <p class="eyebrow">WELCOME BACK</p>
        <h1>上午好，管理员 👋</h1>
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
          <strong>{{ item.value }}</strong>
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
            <h2>最近动态</h2>
            <p>博客最新内容变更</p>
          </div>
          <button class="text-button" type="button" @click="showComingSoon('最近动态')">
            查看全部
          </button>
        </div>
        <ul class="activity-list">
          <li v-for="activity in activities" :key="activity.title">
            <span class="activity-dot" :class="activity.theme"></span>
            <div>
              <strong>{{ activity.title }}</strong>
              <span>{{ activity.time }}</span>
            </div>
          </li>
        </ul>
      </article>
    </section>
  </div>
</template>

<script setup>
import { ChatDotRound, CollectionTag, Document, EditPen, Plus, View } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { showMessage } from '@/composables/util'

defineOptions({ name: 'AdminIndex' })

const router = useRouter()
const today = new Date()
const currentDay = computed(() => String(today.getDate()).padStart(2, '0'))
const currentMonth = computed(() =>
  today.toLocaleDateString('zh-CN', { month: 'short', weekday: 'short' }),
)

const statistics = [
  { label: '文章总数', value: '128', tip: '较上月 +12', icon: Document, theme: 'purple' },
  { label: '总浏览量', value: '36,842', tip: '较上月 +18.6%', icon: View, theme: 'blue' },
  { label: '评论数量', value: '896', tip: '24 条待审核', icon: ChatDotRound, theme: 'orange' },
  { label: '标签数量', value: '42', tip: '覆盖 8 个分类', icon: CollectionTag, theme: 'green' },
]

const quickActions = [
  { label: '发布文章', icon: EditPen, path: '/admin/articles' },
  { label: '新建分类', icon: Plus },
  { label: '管理评论', icon: ChatDotRound },
  { label: '整理标签', icon: CollectionTag },
]

const activities = [
  { title: '发布了文章《Vue 3 组件设计实践》', time: '10 分钟前', theme: 'purple' },
  { title: '“前端开发”分类新增 2 篇文章', time: '2 小时前', theme: 'blue' },
  { title: '审核通过了 6 条评论', time: '昨天 18:35', theme: 'green' },
]

function showComingSoon(name) {
  showMessage(`${name}功能正在建设中`, 'info')
}

function handleQuickAction(action) {
  if (action.path) {
    router.push(action.path)
    return
  }
  showComingSoon(action.label)
}
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
  color: #7c3aed;
  background: #f1eafe;
}

.blue {
  color: #2563eb;
  background: #e9f1ff;
}

.orange {
  color: #ea580c;
  background: #fff0e7;
}

.green {
  color: #059669;
  background: #e7f8f1;
}

.stat-card span {
  color: #8b91a1;
  font-size: 12px;
}

.stat-card strong {
  margin: 1px 0;
  color: #272a36;
  font-size: 22px;
  line-height: 1.35;
}

.stat-card small {
  overflow: hidden;
  color: #a8adba;
  font-size: 10px;
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

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 17px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.activity-list li {
  display: flex;
  align-items: center;
  gap: 12px;
}

.activity-dot {
  width: 9px;
  height: 9px;
  flex: 0 0 auto;
  border-radius: 50%;
}

.activity-list div {
  min-width: 0;
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.activity-list strong {
  overflow: hidden;
  color: #565c6e;
  font-size: 12px;
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-list span:last-child {
  flex: 0 0 auto;
  color: #a3a8b5;
  font-size: 10px;
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

  .activity-list div {
    align-items: flex-start;
    flex-direction: column;
    gap: 2px;
  }
}
</style>
