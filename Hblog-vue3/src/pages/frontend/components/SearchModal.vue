<script setup>
import { Close, Loading, Search } from '@element-plus/icons-vue'
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getSearchResult } from '@/api/frontend/search'

defineOptions({ name: 'SearchModal' })

const visible = defineModel({ type: Boolean, default: false })

const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const searched = ref(false)
const results = ref([])
const total = ref(0)
const inputRef = ref(null)

let debounceTimer = null
let requestSeq = 0

watch(visible, async (open) => {
  if (open) {
    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', onKeydown)
    await nextTick()
    inputRef.value?.focus()
  } else {
    document.body.style.overflow = ''
    window.removeEventListener('keydown', onKeydown)
    resetState()
  }
})

onBeforeUnmount(() => {
  document.body.style.overflow = ''
  window.removeEventListener('keydown', onKeydown)
  clearTimeout(debounceTimer)
})

function resetState() {
  clearTimeout(debounceTimer)
  keyword.value = ''
  loading.value = false
  searched.value = false
  results.value = []
  total.value = 0
}

function close() {
  visible.value = false
}

function onKeydown(e) {
  if (e.key === 'Escape') {
    e.preventDefault()
    close()
  }
}

function onInput() {
  clearTimeout(debounceTimer)
  const word = keyword.value.trim()
  if (!word) {
    searched.value = false
    results.value = []
    total.value = 0
    loading.value = false
    return
  }
  loading.value = true
  searched.value = true
  debounceTimer = setTimeout(() => doSearch(word), 320)
}

async function doSearch(word) {
  const seq = ++requestSeq
  loading.value = true
  searched.value = true
  try {
    const res = await getSearchResult({
      word,
      current: 1,
      size: 10,
    })
    if (seq !== requestSeq) return
    if (res?.success === false) {
      results.value = []
      total.value = 0
      return
    }
    results.value = res.data || []
    total.value = Number(res.total || 0)
  } catch (err) {
    if (seq !== requestSeq) return
    console.error('搜索失败', err)
    results.value = []
    total.value = 0
  } finally {
    if (seq === requestSeq) loading.value = false
  }
}

function goArticle(id) {
  close()
  router.push(`/article/${id}`)
}

/** 去掉后端 Lucene 自带的高亮标签 */
function stripHtml(text = '') {
  return String(text).replace(/<[^>]+>/g, '')
}

function escapeHtml(text = '') {
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

/** 按当前关键词做字符级高亮，避免中文分词只红一部分 */
function highlightTitle(title) {
  const plain = stripHtml(title)
  const word = keyword.value.trim()
  if (!word) return escapeHtml(plain)

  const lowerPlain = plain.toLowerCase()
  const lowerWord = word.toLowerCase()
  const idx = lowerPlain.indexOf(lowerWord)
  if (idx < 0) return escapeHtml(plain)

  const before = escapeHtml(plain.slice(0, idx))
  const match = escapeHtml(plain.slice(idx, idx + word.length))
  const after = escapeHtml(plain.slice(idx + word.length))
  return `${before}<mark class="search-hit">${match}</mark>${after}`
}
</script>

<template>
  <Teleport to="body">
    <Transition name="search-fade">
      <div v-if="visible" class="search-overlay" @click.self="close">
        <div class="search-modal" role="dialog" aria-modal="true" aria-label="文章搜索">
          <header class="search-modal__header">
            <label class="search-field">
              <el-icon
                class="search-field__icon"
                :class="{ 'is-loading': loading }"
                :size="18"
              >
                <Loading v-if="loading" />
                <Search v-else />
              </el-icon>
              <input
                ref="inputRef"
                v-model="keyword"
                class="search-field__input"
                type="search"
                placeholder="请输入关键词搜索..."
                autocomplete="off"
                @input="onInput"
              />
            </label>
            <button type="button" class="search-close" aria-label="关闭" @click="close">
              <el-icon :size="18"><Close /></el-icon>
            </button>
          </header>

          <div class="search-modal__body">
            <!-- 未搜索 -->
            <div v-if="!searched" class="search-hint">
              <p>输入关键词，开始全文搜索</p>
            </div>

            <!-- 搜索中 -->
            <div v-else-if="loading && !results.length" class="search-hint">
              <p>搜索中...</p>
            </div>

            <!-- 无结果 -->
            <div v-else-if="!loading && !results.length" class="search-empty">
              <div class="search-empty__art" aria-hidden="true">
                <svg viewBox="0 0 220 180" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="110" cy="88" r="62" fill="#c8e6c9" />
                  <path
                    d="M58 148h104c0-18-16-30-36-30H94c-20 0-36 12-36 30z"
                    fill="#fff"
                    stroke="#1f2937"
                    stroke-width="3"
                  />
                  <circle cx="110" cy="78" r="28" fill="#1f2937" />
                  <ellipse cx="110" cy="98" rx="22" ry="18" fill="#f5d0c5" />
                  <circle cx="101" cy="94" r="2.2" fill="#1f2937" />
                  <circle cx="119" cy="94" r="2.2" fill="#1f2937" />
                  <path d="M104 102c3 3 9 3 12 0" fill="none" stroke="#1f2937" stroke-width="2" stroke-linecap="round" />
                  <path d="M142 52c6-10 16-12 20-8" fill="none" stroke="#1f2937" stroke-width="2.5" stroke-linecap="round" />
                  <path d="M152 42c4-2 8 0 10 4" fill="none" stroke="#1f2937" stroke-width="2.5" stroke-linecap="round" />
                  <rect x="148" y="28" width="46" height="22" rx="11" fill="#fff" stroke="#1f2937" stroke-width="2" />
                  <text x="171" y="43" text-anchor="middle" font-size="10" font-weight="700" fill="#374151">
                    EMPTY
                  </text>
                </svg>
              </div>
              <p>未查询到结果，换个姿势搜索吧~</p>
            </div>

            <!-- 结果列表 -->
            <ul v-else class="search-list">
              <li v-for="item in results" :key="item.id">
                <button type="button" class="search-item" @click="goArticle(item.id)">
                  <img
                    v-if="item.cover"
                    :src="item.cover"
                    :alt="item.title"
                    class="search-item__cover"
                    loading="lazy"
                  />
                  <div v-else class="search-item__cover search-item__cover--empty">无封面</div>
                  <div class="search-item__meta">
                    <h3 class="search-item__title" v-html="highlightTitle(item.title)"></h3>
                    <p class="search-item__summary">{{ item.summary || '暂无摘要' }}</p>
                    <span v-if="item.createDate" class="search-item__date">{{ item.createDate }}</span>
                  </div>
                </button>
              </li>
            </ul>
          </div>

          <footer class="search-modal__footer">
            <span class="search-esc">
              <kbd>Esc</kbd>
              关闭
            </span>
            <span class="search-engine">基于 Apache Lucene 全文搜索引擎开发</span>
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.search-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 10vh 1rem 2rem;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
}

.search-modal {
  width: min(720px, 100%);
  max-height: min(72vh, 640px);
  display: flex;
  flex-direction: column;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

.search-modal__header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 16px 12px;
  border-bottom: 1px solid #f0f0f0;
}

.search-field {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  height: 44px;
  padding: 0 14px;
  border: 1.5px solid #93c5fd;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12);
}

.search-field__icon {
  color: #9ca3af;
  flex-shrink: 0;
}

.search-field__icon.is-loading {
  color: #3b82f6;
  animation: search-spin 0.8s linear infinite;
}

@keyframes search-spin {
  to {
    transform: rotate(360deg);
  }
}

.search-field__input {
  flex: 1;
  min-width: 0;
  border: 0;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: #111827;
}

.search-field__input::placeholder {
  color: #9ca3af;
}

.search-field__input::-webkit-search-cancel-button {
  display: none;
}

.search-close {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
  flex-shrink: 0;
}

.search-close:hover {
  color: #4b5563;
  background: #f3f4f6;
}

.search-modal__body {
  flex: 1;
  min-height: 280px;
  overflow: auto;
  padding: 8px 0;
}

.search-hint {
  min-height: 280px;
  display: grid;
  place-items: center;
  color: #9ca3af;
  font-size: 14px;
}

.search-empty {
  min-height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 24px;
}

.search-empty__art {
  width: 180px;
  height: 150px;
}

.search-empty__art svg {
  width: 100%;
  height: 100%;
}

.search-empty p {
  margin: 0;
  color: #9ca3af;
  font-size: 14px;
}

.search-list {
  list-style: none;
  margin: 0;
  padding: 4px 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.search-item {
  width: 100%;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 12px;
  border: 0;
  border-radius: 10px;
  background: transparent;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s ease;
}

.search-item:hover {
  background: #f8fafc;
}

.search-item__cover {
  width: 72px;
  height: 54px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  background: #f3f4f6;
}

.search-item__cover--empty {
  display: grid;
  place-items: center;
  color: #9ca3af;
  font-size: 11px;
}

.search-item__meta {
  min-width: 0;
  flex: 1;
}

.search-item__title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search-item__title :deep(.search-hit) {
  color: #ef4444;
  background: transparent;
  font-weight: 700;
  padding: 0;
}

.search-item__summary {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.search-item__date {
  display: inline-block;
  margin-top: 6px;
  font-size: 12px;
  color: #9ca3af;
}

.search-modal__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 16px 12px;
  border-top: 1px solid #f0f0f0;
  color: #9ca3af;
  font-size: 12px;
}

.search-esc {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.search-esc kbd {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 34px;
  height: 22px;
  padding: 0 6px;
  border: 1px solid #e5e7eb;
  border-radius: 5px;
  background: #f9fafb;
  color: #6b7280;
  font-size: 11px;
  font-family: inherit;
  box-shadow: 0 1px 0 #e5e7eb;
}

.search-engine {
  text-align: right;
}

.search-fade-enter-active,
.search-fade-leave-active {
  transition: opacity 0.18s ease;
}

.search-fade-enter-active .search-modal,
.search-fade-leave-active .search-modal {
  transition:
    transform 0.18s ease,
    opacity 0.18s ease;
}

.search-fade-enter-from,
.search-fade-leave-to {
  opacity: 0;
}

.search-fade-enter-from .search-modal,
.search-fade-leave-to .search-modal {
  opacity: 0;
  transform: translateY(-10px) scale(0.98);
}

@media (max-width: 640px) {
  .search-overlay {
    padding: 8vh 0.75rem 1rem;
  }

  .search-engine {
    display: none;
  }
}
</style>
