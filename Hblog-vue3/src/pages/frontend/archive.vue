<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import moment from 'moment'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import CraftAside from './components/CraftAside.vue'
import { getArticlePageList } from '@/api/frontend/article'

gsap.registerPlugin(ScrollTrigger)

const articles = ref([])
const loading = ref(false)
const timelineRef = ref(null)
const progressFillRef = ref(null)

let scrollTriggers = []
let reducedMotion = false

const groupedArchive = computed(() => {
  const map = new Map()

  for (const article of articles.value) {
    const m = moment(article.createTime)
    if (!m.isValid()) continue

    const year = m.format('YYYY')
    const month = m.format('MM')
    const monthLabel = m.format('M月')

    if (!map.has(year)) {
      map.set(year, { year, total: 0, months: new Map() })
    }
    const yearGroup = map.get(year)
    yearGroup.total += 1

    if (!yearGroup.months.has(month)) {
      yearGroup.months.set(month, { month, monthLabel, items: [] })
    }
    yearGroup.months.get(month).items.push(article)
  }

  return [...map.entries()]
    .sort(([a], [b]) => Number(b) - Number(a))
    .map(([, yearGroup]) => ({
      year: yearGroup.year,
      total: yearGroup.total,
      months: [...yearGroup.months.entries()]
        .sort(([a], [b]) => Number(b) - Number(a))
        .map(([, monthGroup]) => monthGroup),
    }))
})

const totalCount = computed(() => articles.value.length)

const yearSpan = computed(() => {
  if (!articles.value.length) return ''
  const years = groupedArchive.value.map((g) => g.year)
  if (years.length === 1) return years[0]
  return `${years[years.length - 1]} – ${years[0]}`
})

function formatDay(value) {
  if (!value) return ''
  return moment(value).format('MM-DD')
}

async function fetchAllArticles() {
  loading.value = true
  try {
    const pageSize = 50
    let current = 1
    let total = Infinity
    const list = []

    while (list.length < total) {
      const res = await getArticlePageList({ current, size: pageSize })
      if (res?.success === false) break

      const batch = res.data || []
      list.push(...batch)
      total = Number(res.total ?? list.length)
      if (!batch.length || batch.length < pageSize) break
      current += 1
      if (current > 40) break
    }

    articles.value = list
  } catch (err) {
    console.error('加载归档失败', err)
  } finally {
    loading.value = false
  }
}

function prefersReducedMotion() {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function clearAnimations() {
  scrollTriggers.forEach((st) => st.kill())
  scrollTriggers = []
  if (timelineRef.value) {
    gsap.killTweensOf(timelineRef.value.querySelectorAll('.archive-anim'))
  }
  if (progressFillRef.value) {
    gsap.killTweensOf(progressFillRef.value)
  }
}

function setupAnimations() {
  clearAnimations()
  const root = timelineRef.value
  const fill = progressFillRef.value
  if (!root || !fill || !articles.value.length) return

  reducedMotion = prefersReducedMotion()

  if (reducedMotion) {
    gsap.set(root.querySelectorAll('.archive-anim'), { clearProps: 'all' })
    gsap.set(fill, { scaleY: 1 })
    return
  }

  gsap.set(fill, { scaleY: 0, transformOrigin: 'top center' })

  const progressTween = gsap.to(fill, {
    scaleY: 1,
    ease: 'none',
    scrollTrigger: {
      trigger: root,
      start: 'top 30%',
      end: 'bottom 70%',
      scrub: 0.45,
    },
  })
  if (progressTween.scrollTrigger) scrollTriggers.push(progressTween.scrollTrigger)

  const yearNodes = root.querySelectorAll('.archive-year')
  yearNodes.forEach((node) => {
    const tween = gsap.fromTo(
      node.querySelectorAll('.archive-anim'),
      { opacity: 0, y: 22 },
      {
        opacity: 1,
        y: 0,
        duration: 0.55,
        stagger: 0.05,
        ease: 'power2.out',
        clearProps: 'transform',
        scrollTrigger: {
          trigger: node,
          start: 'top 82%',
          toggleActions: 'play none none none',
        },
      },
    )
    if (tween.scrollTrigger) scrollTriggers.push(tween.scrollTrigger)
  })

  ScrollTrigger.refresh()
}

onMounted(async () => {
  await fetchAllArticles()
  await nextTick()
  setupAnimations()
})

onBeforeUnmount(() => {
  clearAnimations()
})
</script>

<template>
  <div class="craft-layout">
    <CraftAside />

    <section class="craft-main" v-loading="loading">
      <header class="archive-head">
        <h2 class="craft-section-title">归档</h2>
        <p v-if="totalCount" class="archive-head__meta">
          <span>{{ totalCount }} 篇文章</span>
          <span v-if="yearSpan" class="archive-head__dot" aria-hidden="true">·</span>
          <span v-if="yearSpan">{{ yearSpan }}</span>
        </p>
      </header>

      <div v-if="groupedArchive.length" ref="timelineRef" class="archive-timeline">
        <div class="archive-rail" aria-hidden="true">
          <div class="archive-rail__track" />
          <div ref="progressFillRef" class="archive-rail__fill" />
        </div>

        <div class="archive-body">
          <section
            v-for="yearGroup in groupedArchive"
            :key="yearGroup.year"
            class="archive-year"
          >
            <div class="archive-year__head archive-anim">
              <span class="archive-year__dot" aria-hidden="true" />
              <h3 class="archive-year__title">{{ yearGroup.year }}</h3>
              <span class="archive-year__count">{{ yearGroup.total }} 篇</span>
            </div>

            <div
              v-for="monthGroup in yearGroup.months"
              :key="`${yearGroup.year}-${monthGroup.month}`"
              class="archive-month"
            >
              <h4 class="archive-month__label archive-anim">{{ monthGroup.monthLabel }}</h4>
              <ul class="archive-list">
                <li
                  v-for="article in monthGroup.items"
                  :key="article.id"
                  class="archive-item archive-anim"
                >
                  <time class="archive-item__date" :datetime="article.createTime">
                    {{ formatDay(article.createTime) }}
                  </time>
                  <RouterLink :to="`/article/${article.id}`" class="archive-item__link">
                    <span class="archive-item__title">{{ article.title }}</span>
                    <span v-if="article.categoryName" class="archive-item__cat">
                      {{ article.categoryName }}
                    </span>
                  </RouterLink>
                </li>
              </ul>
            </div>
          </section>
        </div>
      </div>

      <div v-else-if="!loading" class="craft-empty">暂无文章可归档。</div>
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
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #0f0f0f;
}

.archive-head {
  margin-bottom: 2rem;
}

.archive-head__meta {
  margin: 0.55rem 0 0;
  font-size: 0.9rem;
  color: #8a8a8a;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.35rem;
}

.archive-head__dot {
  opacity: 0.55;
}

.archive-timeline {
  position: relative;
  display: grid;
  grid-template-columns: 18px minmax(0, 1fr);
  gap: 1.1rem;
}

.archive-rail {
  position: relative;
  width: 18px;
}

.archive-rail__track,
.archive-rail__fill {
  position: absolute;
  left: 8px;
  top: 0.7rem;
  bottom: 0.4rem;
  width: 2px;
  border-radius: 999px;
}

.archive-rail__track {
  background: #ebe7df;
}

.archive-rail__fill {
  background: linear-gradient(180deg, #2a2a2a 0%, #7a7368 100%);
  transform: scaleY(0);
  transform-origin: top center;
  will-change: transform;
}

.archive-body {
  min-width: 0;
  padding-bottom: 0.5rem;
}

.archive-year + .archive-year {
  margin-top: 2.4rem;
}

.archive-year__head {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
  margin-bottom: 1.15rem;
  position: relative;
}

.archive-year__dot {
  position: absolute;
  left: calc(-1.1rem - 18px + 5px);
  top: 0.62rem;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #0f0f0f;
  box-shadow: 0 0 0 4px #fff;
  z-index: 1;
}

.archive-year__title {
  margin: 0;
  font-size: 1.65rem;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: #0f0f0f;
  line-height: 1.1;
}

.archive-year__count {
  font-size: 0.8rem;
  color: #9a9a9a;
}

.archive-month + .archive-month {
  margin-top: 1.35rem;
}

.archive-month__label {
  margin: 0 0 0.55rem;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: #8a8a8a;
  text-transform: none;
}

.archive-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.archive-item {
  display: grid;
  grid-template-columns: 3.4rem minmax(0, 1fr);
  gap: 0.85rem;
  align-items: baseline;
  padding: 0.55rem 0;
  border-bottom: 1px solid #f0ece4;
}

.archive-item:last-child {
  border-bottom: none;
}

.archive-item__date {
  font-size: 0.8rem;
  color: #9a9a9a;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.02em;
}

.archive-item__link {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.55rem 0.85rem;
  color: inherit;
  text-decoration: none;
  min-width: 0;
}

.archive-item__title {
  font-size: 0.98rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.45;
  transition: color 0.2s ease;
}

.archive-item__link:hover .archive-item__title {
  color: #0f0f0f;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.archive-item__cat {
  font-size: 0.75rem;
  color: #9a9a9a;
  white-space: nowrap;
}

.craft-empty {
  padding: 3rem 1rem;
  text-align: center;
  color: #8a8a8a;
  font-size: 0.95rem;
}

@media (min-width: 640px) {
  .archive-timeline {
    grid-template-columns: 22px minmax(0, 1fr);
    gap: 1.35rem;
  }

  .archive-year__dot {
    left: calc(-1.35rem - 22px + 7px);
  }

  .archive-item {
    grid-template-columns: 3.75rem minmax(0, 1fr);
    gap: 1.1rem;
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

@media (prefers-reduced-motion: reduce) {
  .archive-rail__fill {
    transform: none;
  }
}
</style>
