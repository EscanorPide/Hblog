<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: {
    type: Number,
    default: 1,
  },
  pageSize: {
    type: Number,
    default: 10,
  },
  total: {
    type: Number,
    default: 0,
  },
  pagerCount: {
    type: Number,
    default: 5,
  },
})

const emit = defineEmits(['change'])

const pageCount = computed(() => Math.max(1, Math.ceil((props.total || 0) / (props.pageSize || 1))))

const pages = computed(() => {
  const total = pageCount.value
  const current = props.current
  const max = Math.min(props.pagerCount, total)

  if (total <= max) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }

  let start = Math.max(1, current - Math.floor(max / 2))
  let end = start + max - 1
  if (end > total) {
    end = total
    start = end - max + 1
  }
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

const showPrev = computed(() => props.current > 1)
const showNext = computed(() => props.current < pageCount.value)
const visible = computed(() => pageCount.value > 1)

function go(page) {
  if (page < 1 || page > pageCount.value || page === props.current) return
  emit('change', page)
}
</script>

<template>
  <nav v-if="visible" class="mini-pager" aria-label="分页">
    <button
      v-if="showPrev"
      type="button"
      class="mini-pager__item mini-pager__arrow"
      aria-label="上一页"
      @click="go(current - 1)"
    >
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
        <path d="M10 3L5 8l5 5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
      </svg>
    </button>

    <button
      v-for="page in pages"
      :key="page"
      type="button"
      class="mini-pager__item mini-pager__page"
      :class="{ 'is-active': page === current }"
      :aria-current="page === current ? 'page' : undefined"
      @click="go(page)"
    >
      {{ page }}
    </button>

    <button
      v-if="showNext"
      type="button"
      class="mini-pager__item mini-pager__arrow"
      aria-label="下一页"
      @click="go(current + 1)"
    >
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
        <path d="M6 3l5 5-5 5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
      </svg>
    </button>
  </nav>
</template>

<style scoped>
.mini-pager {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 1.75rem;
  user-select: none;
}

.mini-pager__item {
  all: unset;
  box-sizing: border-box;
  font-family: 'DM Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  cursor: pointer;
  color: #8a8a8a;
  transition: color 0.15s ease;
}

.mini-pager__item:hover {
  color: #2f2f2f;
}

.mini-pager__page {
  min-width: 2rem;
  height: 2rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.95rem;
  font-weight: 400;
  line-height: 1;
  border-radius: 4px;
  border: 1px solid transparent;
  background: transparent;
}

.mini-pager__page.is-active {
  color: #2f2f2f;
  border-color: #d0d0d0;
  background: #fff;
  cursor: default;
}

.mini-pager__page.is-active:hover {
  color: #2f2f2f;
}

.mini-pager__arrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.5rem;
  height: 1.5rem;
  color: #8a8a8a;
}

.mini-pager__arrow:hover {
  color: #2f2f2f;
}
</style>
