<template>
  <div ref="dashRef" class="dash" v-loading="loading">
    <!-- 顶部 KPI -->
    <section class="kpi-row">
      <article v-for="item in kpiList" :key="item.label" class="kpi-card anim-item">
        <div class="kpi-top">
          <span>{{ item.label }}</span>
          <span class="kpi-badge" :class="item.trend">
            <el-icon :size="12">
              <Top v-if="item.trend === 'up'" />
              <Bottom v-else />
            </el-icon>
          </span>
        </div>
        <strong v-if="item.pair">
          <CountTo :value="item.left" :duration="1.6" />
          <span class="kpi-slash">/</span>
          <CountTo :value="item.right" :duration="1.8" />
        </strong>
        <strong v-else>
          <CountTo :value="item.value" :duration="item.duration || 1.6" />
        </strong>
      </article>
      <button type="button" class="kpi-refresh anim-item" :disabled="loading" @click="fetchAll">
        <el-icon :size="16" :class="{ spin: loading }"><Refresh /></el-icon>
      </button>
    </section>

    <!-- 中部：Activity + 侧栏 -->
    <section class="mid-grid">
      <article class="panel activity-panel anim-item">
        <div class="panel-head">
          <div>
            <h2>访问趋势</h2>
            <p>近 7 日 PV · 约每小时更新</p>
          </div>
          <span class="pill">近一周</span>
        </div>
        <div ref="pvChartRef" class="chart-box"></div>
      </article>

      <div class="side-col">
        <article class="promo-card anim-item">
          <div class="promo-copy">
            <h3>内容运营提示</h3>
            <p>保持稳定更新，浏览与互动会更健康。</p>
            <el-button class="promo-btn" @click="router.push('/admin/articles')">
              去写文章
            </el-button>
          </div>
          <div ref="ringChartRef" class="promo-ring"></div>
        </article>

        <article class="panel rank-panel anim-item">
          <div class="panel-head">
            <h2>发布高峰日</h2>
          </div>
          <ul v-if="topPublishDays.length" class="rank-list">
            <li v-for="(item, index) in topPublishDays" :key="item.date" class="rank-item">
              <span class="rank-avatar" :style="{ background: rankColors[index % rankColors.length] }">
                {{ index + 1 }}
              </span>
              <div class="rank-main">
                <strong>{{ item.date }}</strong>
                <span>
                  发布
                  <CountTo :value="item.count" :duration="1.2" />
                  篇
                </span>
              </div>
              <em>
                <CountTo :value="item.percent" :duration="1.4" separator="" />%
              </em>
            </li>
          </ul>
          <p v-else class="empty-tip">暂无发布数据</p>
        </article>
      </div>
    </section>

    <!-- 底部：发布热力图（整行铺满） -->
    <section class="panel heatmap-panel anim-item">
      <div class="panel-head">
        <div>
          <h2>文章发布热点</h2>
          <p>近一年每日发布密度</p>
        </div>
      </div>
      <div ref="publishChartRef" class="chart-box chart-box--heatmap"></div>
    </section>
  </div>
</template>

<script setup>
import { Bottom, Refresh, Top } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import gsap from 'gsap'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import {
  getBaseStatisticsInfo,
  getPublishArticleStatisticsInfo,
  getPvStatisticsInfo,
} from '@/api/admin/dashboard'
import CountTo from '@/components/CountTo.vue'
import { showMessage } from '@/composables/util'

defineOptions({ name: 'AdminStatistics' })

const router = useRouter()
const loading = ref(false)

const dashRef = ref(null)
const pvChartRef = ref(null)
const publishChartRef = ref(null)
const ringChartRef = ref(null)

const pvChart = shallowRef(null)
const publishChart = shallowRef(null)
const ringChart = shallowRef(null)

const pvDates = ref([])
const pvCounts = ref([])
const publishMap = ref({})

const stats = reactive({
  articleTotalCount: 0,
  categoryTotalCount: 0,
  tagTotalCount: 0,
  pvTotalCount: 0,
})

const rankColors = ['#6366f1', '#8b5cf6', '#a78bfa', '#c4b5fd', '#818cf8']

const weekPvTotal = computed(() =>
  (pvCounts.value || []).reduce((sum, n) => sum + Number(n || 0), 0),
)

const weekPvTrend = computed(() => {
  const arr = pvCounts.value || []
  if (arr.length < 2) return 0
  const mid = Math.floor(arr.length / 2)
  const prev = arr.slice(0, mid).reduce((s, n) => s + Number(n || 0), 0)
  const next = arr.slice(mid).reduce((s, n) => s + Number(n || 0), 0)
  if (prev === 0) return next > 0 ? 100 : 0
  return Math.round(((next - prev) / prev) * 100)
})

const kpiList = computed(() => [
  {
    label: '总浏览量',
    value: stats.pvTotalCount,
    trend: weekPvTrend.value >= 0 ? 'up' : 'down',
    duration: 2,
  },
  {
    label: '近一周 PV',
    value: weekPvTotal.value,
    trend: weekPvTrend.value >= 0 ? 'up' : 'down',
    duration: 1.8,
  },
  {
    label: '文章总数',
    value: stats.articleTotalCount,
    trend: 'up',
    duration: 1.5,
  },
  {
    label: '分类 / 标签',
    pair: true,
    left: stats.categoryTotalCount,
    right: stats.tagTotalCount,
    trend: 'up',
  },
])

const topPublishDays = computed(() => {
  const entries = Object.entries(publishMap.value || {})
    .map(([date, count]) => ({ date: String(date), count: Number(count || 0) }))
    .filter((item) => item.count > 0)
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)

  const max = Math.max(1, ...entries.map((item) => item.count))
  return entries.map((item) => ({
    ...item,
    percent: Math.round((item.count / max) * 100),
  }))
})

function formatDateKey(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function renderPvChart() {
  if (!pvChartRef.value) return
  if (!pvChart.value) pvChart.value = echarts.init(pvChartRef.value)

  const peakIndex = pvCounts.value.reduce(
    (best, n, i, arr) => (Number(n) > Number(arr[best] || 0) ? i : best),
    0,
  )
  const peakValue = Number(pvCounts.value[peakIndex] || 0)
  const peakDate = pvDates.value[peakIndex] || ''

  pvChart.value.setOption({
    color: ['#6366f1'],
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 12 },
      extraCssText: 'box-shadow: 0 10px 30px rgba(15,23,42,0.08); border-radius: 12px;',
      formatter(params) {
        const p = params?.[0]
        if (!p) return ''
        return `<div style="padding:2px 4px"><div style="color:#9ca3af;font-size:11px;margin-bottom:4px">${p.axisValue}</div><strong style="font-size:16px;color:#111827">${Number(p.value).toLocaleString('zh-CN')}</strong><div style="color:#6b7280;font-size:11px;margin-top:2px">PV / 日</div></div>`
      },
    },
    grid: { left: 48, right: 20, top: 28, bottom: 36 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: pvDates.value,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#9ca3af', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 },
    },
    series: [
      {
        name: 'PV',
        type: 'line',
        smooth: 0.45,
        symbol: 'circle',
        symbolSize: 10,
        showSymbol: true,
        lineStyle: { width: 3, color: '#6366f1' },
        itemStyle: {
          color: '#fff',
          borderColor: '#6366f1',
          borderWidth: 3,
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(99, 102, 241, 0.28)' },
            { offset: 1, color: 'rgba(99, 102, 241, 0.02)' },
          ]),
        },
        markPoint:
          peakValue > 0
            ? {
                symbol: 'circle',
                symbolSize: 1,
                label: {
                  show: true,
                  formatter: `${peakValue.toLocaleString('zh-CN')}\nPV`,
                  color: '#4338ca',
                  fontSize: 11,
                  fontWeight: 600,
                  backgroundColor: '#fff',
                  borderColor: '#e0e7ff',
                  borderWidth: 1,
                  borderRadius: 10,
                  padding: [8, 10],
                  lineHeight: 16,
                  shadowColor: 'rgba(15,23,42,0.08)',
                  shadowBlur: 12,
                },
                data: [{ coord: [peakDate, peakValue], name: 'peak' }],
              }
            : undefined,
        data: pvCounts.value,
      },
    ],
  })
}

function renderRingChart() {
  if (!ringChartRef.value) return
  if (!ringChart.value) ringChart.value = echarts.init(ringChartRef.value)

  const article = Number(stats.articleTotalCount || 0)
  const category = Number(stats.categoryTotalCount || 0)
  const tag = Number(stats.tagTotalCount || 0)
  const total = Math.max(1, article + category + tag)

  ringChart.value.setOption({
    series: [
      {
        type: 'pie',
        radius: ['62%', '88%'],
        center: ['50%', '50%'],
        silent: true,
        label: { show: false },
        data: [
          { value: article || 1, itemStyle: { color: '#6366f1' } },
          { value: category || 1, itemStyle: { color: '#8b5cf6' } },
          { value: tag || 1, itemStyle: { color: '#c4b5fd' } },
          {
            value: total * 0.35,
            itemStyle: { color: '#eef2ff' },
          },
        ],
      },
    ],
  })
}

function renderPublishChart() {
  if (!publishChartRef.value) return
  if (!publishChart.value) publishChart.value = echarts.init(publishChartRef.value)

  const entries = Object.entries(publishMap.value || {})
  if (!entries.length) {
    publishChart.value.clear()
    publishChart.value.setOption(
      {
        title: {
          text: '暂无近一年发布数据',
          left: 'center',
          top: 'middle',
          textStyle: { color: '#9ca3af', fontSize: 14, fontWeight: 400 },
        },
      },
      true,
    )
    return
  }

  const countMap = new Map(
    entries.map(([date, count]) => [String(date).slice(0, 10), Number(count || 0)]),
  )
  const end = new Date()
  const start = new Date()
  start.setFullYear(end.getFullYear() - 1)

  const heatmapData = []
  const cursor = new Date(start)
  while (cursor <= end) {
    const key = formatDateKey(cursor)
    heatmapData.push([key, countMap.get(key) || 0])
    cursor.setDate(cursor.getDate() + 1)
  }

  const maxCount = Math.max(1, ...heatmapData.map((item) => item[1]))

  publishChart.value.setOption(
    {
      title: { show: false },
      tooltip: {
        formatter(params) {
          const [date, count] = params.value || []
          return `${date}<br/>发布 <strong>${count}</strong> 篇`
        },
      },
      visualMap: {
        show: false,
        min: 0,
        max: maxCount,
        inRange: {
          color: ['#eef2ff', '#c7d2fe', '#818cf8', '#4f46e5'],
        },
      },
      calendar: {
        top: 28,
        left: 56,
        right: 24,
        bottom: 16,
        cellSize: ['auto', 18],
        range: [formatDateKey(start), formatDateKey(end)],
        itemStyle: {
          borderWidth: 3,
          borderColor: '#fff',
          borderRadius: 3,
        },
        yearLabel: { show: false },
        dayLabel: {
          color: '#9ca3af',
          nameMap: 'cn',
          fontSize: 11,
        },
        monthLabel: {
          color: '#6b7280',
          nameMap: 'cn',
          fontSize: 12,
        },
        splitLine: { show: false },
      },
      series: [
        {
          type: 'heatmap',
          coordinateSystem: 'calendar',
          data: heatmapData,
        },
      ],
    },
    true,
  )
}

async function fetchAll() {
  loading.value = true
  try {
    const [baseRes, pvRes, publishRes] = await Promise.all([
      getBaseStatisticsInfo(),
      getPvStatisticsInfo(),
      getPublishArticleStatisticsInfo(),
    ])

    if (baseRes?.success !== false && baseRes?.data) {
      stats.articleTotalCount = Number(baseRes.data.articleTotalCount || 0)
      stats.categoryTotalCount = Number(baseRes.data.categoryTotalCount || 0)
      stats.tagTotalCount = Number(baseRes.data.tagTotalCount || 0)
      stats.pvTotalCount = Number(baseRes.data.pvTotalCount || 0)
    }

    if (pvRes?.success !== false) {
      pvDates.value = pvRes.data?.pvDates || []
      pvCounts.value = (pvRes.data?.pvCounts || []).map((n) => Number(n || 0))
    }

    if (publishRes?.success !== false) {
      publishMap.value = publishRes.data || {}
    }

    await nextTick()
    renderPvChart()
    renderRingChart()
    renderPublishChart()
  } catch (err) {
    console.error('获取数据统计失败', err)
    showMessage('获取数据统计失败', 'error')
  } finally {
    loading.value = false
  }
}

function handleResize() {
  pvChart.value?.resize()
  publishChart.value?.resize()
  ringChart.value?.resize()
}

function playEnterAnimation() {
  const root = dashRef.value
  if (!root) return
  const items = root.querySelectorAll('.anim-item')
  gsap.killTweensOf(items)
  gsap.fromTo(
    items,
    { opacity: 0, y: 18 },
    {
      opacity: 1,
      y: 0,
      duration: 0.55,
      stagger: 0.07,
      ease: 'power2.out',
      clearProps: 'transform',
    },
  )
}

onMounted(async () => {
  await fetchAll()
  await nextTick()
  playEnterAnimation()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (dashRef.value) gsap.killTweensOf(dashRef.value.querySelectorAll('.anim-item'))
  pvChart.value?.dispose()
  publishChart.value?.dispose()
  ringChart.value?.dispose()
  pvChart.value = null
  publishChart.value = null
  ringChart.value = null
})
</script>

<style scoped>
.dash {
  display: flex;
  flex-direction: column;
  gap: 18px;
  color: #303442;
}

/* KPI */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr)) auto;
  gap: 14px;
  align-items: stretch;
}

.kpi-card {
  padding: 18px 20px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #eef0f4;
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.04);
}

.kpi-refresh {
  width: 48px;
  min-height: 100%;
  display: grid;
  place-items: center;
  border: 1px solid #eef0f4;
  border-radius: 18px;
  background: #fff;
  color: #6b7280;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.04);
  transition:
    color 0.15s,
    background 0.15s;
}

.kpi-refresh:hover:not(:disabled) {
  color: #4f46e5;
  background: #f5f3ff;
}

.kpi-refresh:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.kpi-refresh .spin {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.kpi-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #8b91a1;
  font-size: 13px;
}

.kpi-badge {
  width: 22px;
  height: 22px;
  display: grid;
  place-items: center;
  border-radius: 50%;
}

.kpi-badge.up {
  color: #059669;
  background: #ecfdf5;
}

.kpi-badge.down {
  color: #dc2626;
  background: #fef2f2;
}

.kpi-card strong {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  font-size: 34px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #1f2430;
  line-height: 1.15;
}

.kpi-card strong :deep(.count-to) {
  font-size: inherit;
  font-weight: inherit;
  letter-spacing: inherit;
}

.kpi-slash {
  margin: 0 2px;
  color: #c4c8d4;
  font-weight: 600;
}

/* mid */
.mid-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(280px, 0.9fr);
  gap: 16px;
  align-items: stretch;
}

.panel {
  padding: 20px 22px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid #eef0f4;
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.04);
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.panel-head h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1f2430;
}

.panel-head p {
  margin: 4px 0 0;
  color: #a3a8b5;
  font-size: 12px;
}

.pill {
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f5f3ff;
  color: #6d28d9;
  font-size: 12px;
  font-weight: 600;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.chart-box--heatmap {
  height: 280px;
}

.heatmap-panel {
  width: 100%;
}

.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.promo-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 20px;
  border-radius: 20px;
  background:
    radial-gradient(circle at 88% 20%, rgba(139, 92, 246, 0.18), transparent 34%),
    linear-gradient(135deg, #f5f3ff 0%, #eef2ff 55%, #fff 100%);
  border: 1px solid #e9e5ff;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.08);
}

.promo-copy {
  min-width: 0;
  flex: 1;
}

.promo-copy h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #4c1d95;
}

.promo-copy p {
  margin: 8px 0 14px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.55;
}

.promo-btn {
  border: 0;
  border-radius: 999px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  font-weight: 600;
}

.promo-btn:hover {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
}

.promo-ring {
  width: 108px;
  height: 108px;
  flex-shrink: 0;
}

.rank-panel {
  flex: 1;
}

.rank-list {
  list-style: none;
  margin: 0;
  padding: 4px 0 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rank-list li {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-avatar {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.rank-main {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.rank-main strong {
  font-size: 13px;
  color: #303442;
  font-weight: 600;
}

.rank-main span {
  margin-top: 2px;
  font-size: 11px;
  color: #a3a8b5;
}

.rank-list em {
  display: inline-flex;
  align-items: baseline;
  font-style: normal;
  font-size: 16px;
  font-weight: 700;
  color: #4f46e5;
}

.rank-list em :deep(.count-to),
.rank-main span :deep(.count-to) {
  font-size: inherit;
  font-weight: inherit;
}

.empty-tip {
  margin: 28px 0;
  text-align: center;
  color: #a3a8b5;
  font-size: 13px;
}

@media (max-width: 1200px) {
  .kpi-row {
    grid-template-columns: repeat(2, minmax(0, 1fr)) auto;
  }

  .mid-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .kpi-row {
    grid-template-columns: 1fr 1fr;
  }

  .kpi-refresh {
    grid-column: 1 / -1;
    width: 100%;
    min-height: 44px;
  }

  .chart-box {
    height: 240px;
  }

  .chart-box--heatmap {
    height: 240px;
  }
}
</style>
