<template>
  <section class="comment-section" aria-label="评论区">
    <header class="comment-header">
      <h2 class="comment-title">评论</h2>
      <span class="comment-count">{{ total }} 条</span>
    </header>

    <!-- 发表 / 回复表单 -->
    <el-form
      ref="formRef"
      class="comment-form"
      :model="form"
      :rules="rules"
      label-position="top"
      @submit.prevent="handleSubmit"
    >
      <div v-if="replyTarget" class="reply-tip">
        <span>
          回复
          <strong>{{ replyTarget.nickname }}</strong>
        </span>
        <button type="button" class="reply-cancel" @click="clearReply">取消</button>
      </div>

      <el-form-item label="评论内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          maxlength="120"
          show-word-limit
          :placeholder="replyTarget ? `回复 ${replyTarget.nickname}…` : '说点什么吧…'"
        />
      </el-form-item>

      <!-- 已登录：昵称/邮箱来自 Pinia，无需填写 -->
      <div v-if="isLoggedIn" class="comment-logged">
        <div class="comment-logged__avatar" aria-hidden="true">
          {{ avatarText(displayNickname) }}
        </div>
        <div class="comment-logged__meta">
          <strong>{{ displayNickname }}</strong>
          <span>已登录，将以此身份发表评论</span>
        </div>
      </div>

      <div v-else class="comment-form__row">
        <el-form-item label="昵称" prop="nickname" class="comment-form__field">
          <el-input v-model="form.nickname" maxlength="60" clearable placeholder="怎么称呼你" />
        </el-form-item>
        <el-form-item label="邮箱" prop="mail" class="comment-form__field">
          <el-input v-model="form.mail" maxlength="60" clearable placeholder="不会公开展示" />
        </el-form-item>
        <el-form-item label="网站" prop="website" class="comment-form__field">
          <el-input v-model="form.website" maxlength="60" clearable placeholder="选填" />
        </el-form-item>
      </div>

      <div v-if="isLoggedIn" class="comment-form__row comment-form__row--single">
        <el-form-item label="网站" prop="website" class="comment-form__field">
          <el-input v-model="form.website" maxlength="60" clearable placeholder="选填" />
        </el-form-item>
      </div>

      <div class="comment-form__actions">
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ replyTarget ? '提交回复' : '发表评论' }}
        </el-button>
        <p class="comment-hint">
          {{ isLoggedIn ? '提交后需审核通过才会展示' : '提交后需审核通过才会展示 · 登录后可免填昵称邮箱' }}
        </p>
      </div>
    </el-form>

    <!-- 评论列表 -->
    <div v-loading="loading" class="comment-list">
      <template v-if="commentTree.length">
        <article
          v-for="item in commentTree"
          :key="item.id"
          class="comment-item"
        >
          <div class="comment-avatar" aria-hidden="true">
            <img v-if="item.avatar" :src="item.avatar" alt="" />
            <span v-else>{{ avatarText(item.nickname) }}</span>
          </div>
          <div class="comment-body">
            <div class="comment-meta">
              <a
                v-if="item.website"
                class="comment-name comment-name--link"
                :href="normalizeWebsite(item.website)"
                target="_blank"
                rel="noopener noreferrer"
              >
                {{ item.nickname }}
              </a>
              <span v-else class="comment-name">{{ item.nickname }}</span>
              <time class="comment-time">{{ formatTime(item.createTime) }}</time>
            </div>
            <p class="comment-content">{{ item.content }}</p>
            <button type="button" class="comment-reply-btn" @click="startReply(item)">
              回复
            </button>

            <!-- 子评论 -->
            <div v-if="item.children?.length" class="comment-children">
              <article
                v-for="child in item.children"
                :key="child.id"
                class="comment-item comment-item--child"
              >
                <div class="comment-avatar comment-avatar--sm" aria-hidden="true">
                  <img v-if="child.avatar" :src="child.avatar" alt="" />
                  <span v-else>{{ avatarText(child.nickname) }}</span>
                </div>
                <div class="comment-body">
                  <div class="comment-meta">
                    <a
                      v-if="child.website"
                      class="comment-name comment-name--link"
                      :href="normalizeWebsite(child.website)"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {{ child.nickname }}
                    </a>
                    <span v-else class="comment-name">{{ child.nickname }}</span>
                    <template v-if="child.replyNickname">
                      <span class="comment-reply-arrow">回复</span>
                      <span class="comment-reply-name">{{ child.replyNickname }}</span>
                    </template>
                    <time class="comment-time">{{ formatTime(child.createTime) }}</time>
                  </div>
                  <p class="comment-content">{{ child.content }}</p>
                  <button type="button" class="comment-reply-btn" @click="startReply(child, item)">
                    回复
                  </button>
                </div>
              </article>
            </div>
          </div>
        </article>
      </template>

      <div v-else-if="!loading" class="comment-empty">暂无评论，来抢沙发吧</div>
    </div>

    <div v-if="total > pagination.size" class="comment-pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[10, 20]"
        layout="total, prev, pager, next"
        background
        @current-change="fetchList"
        @size-change="handleSizeChange"
      />
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import moment from 'moment'
import { storeToRefs } from 'pinia'
import { addComment, getCommentPageList } from '@/api/frontend/comment'
import { getToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  /** 评论所属路由，如 /article/1 */
  routerUrl: {
    type: String,
    required: true,
  },
})

const STORAGE_KEY = 'hblog_comment_profile'

const userStore = useUserStore()
const { userInfo, nickname: storeNickname, mail: storeMail } = storeToRefs(userStore)

const isLoggedIn = computed(() => Boolean(getToken() && (userInfo.value?.username || storeNickname.value)))
const displayNickname = computed(() => storeNickname.value || userInfo.value?.username || '用户')

const loading = ref(false)
const submitting = ref(false)
const formRef = ref()
const flatList = ref([])
const total = ref(0)
const replyTarget = ref(null)
const replyParentId = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
})

const form = reactive({
  content: '',
  nickname: '',
  mail: '',
  website: '',
})

const rules = computed(() => {
  const base = {
    content: [
      { required: true, message: '请输入评论内容', trigger: 'blur' },
      { max: 120, message: '评论内容不能超过 120 字', trigger: 'blur' },
    ],
    website: [{ max: 60, message: '网站不能超过 60 字', trigger: 'blur' }],
  }
  if (isLoggedIn.value) return base
  return {
    ...base,
    nickname: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { max: 60, message: '昵称不能超过 60 字', trigger: 'blur' },
    ],
    mail: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
      { max: 60, message: '邮箱不能超过 60 字', trigger: 'blur' },
    ],
  }
})

/** 同步登录用户资料到表单 */
function syncLoggedInProfile() {
  if (!isLoggedIn.value) return
  form.nickname = displayNickname.value
  const uname = String(userInfo.value?.username || 'user').slice(0, 48)
  form.mail = storeMail.value || `${uname}@hblog.local`
}

const nicknameMap = computed(() => {
  const map = {}
  flatList.value.forEach((item) => {
    map[item.id] = item.nickname
  })
  return map
})

/** 将扁平列表组装为两级树：根评论 + 子回复 */
const commentTree = computed(() => {
  const roots = []
  const childrenMap = {}

  flatList.value.forEach((item) => {
    const node = {
      ...item,
      replyNickname: item.replyCommentId ? nicknameMap.value[item.replyCommentId] : '',
      children: [],
    }
    if (!item.parentCommentId) {
      roots.push(node)
    } else {
      if (!childrenMap[item.parentCommentId]) {
        childrenMap[item.parentCommentId] = []
      }
      childrenMap[item.parentCommentId].push(node)
    }
  })

  roots.forEach((root) => {
    root.children = childrenMap[root.id] || []
  })

  // 孤儿回复（父评论不在当前页）降级展示为根
  Object.entries(childrenMap).forEach(([parentId, children]) => {
    if (!roots.find((r) => String(r.id) === String(parentId))) {
      roots.push(...children)
    }
  })

  return roots
})

function formatTime(time) {
  if (!time) return ''
  return moment(time).format('YYYY-MM-DD HH:mm')
}

function avatarText(name) {
  const text = (name || '?').trim()
  return text.slice(0, 1).toUpperCase()
}

function normalizeWebsite(url) {
  if (!url) return ''
  if (/^https?:\/\//i.test(url)) return url
  return `https://${url}`
}

function loadProfile() {
  // 已登录优先用 Pinia 用户信息
  if (isLoggedIn.value) {
    syncLoggedInProfile()
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (raw) {
        const data = JSON.parse(raw)
        form.website = data.website || ''
      }
    } catch {
      // ignore
    }
    return
  }
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    form.nickname = data.nickname || ''
    form.mail = data.mail || ''
    form.website = data.website || ''
  } catch {
    // ignore
  }
}

function saveProfile() {
  // 登录态只持久化选填网站；昵称邮箱走账号
  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      nickname: isLoggedIn.value ? displayNickname.value : form.nickname.trim(),
      mail: isLoggedIn.value ? form.mail.trim() : form.mail.trim(),
      website: form.website.trim(),
    }),
  )
}

function fetchList() {
  if (!props.routerUrl || loading.value) return

  loading.value = true
  getCommentPageList({
    current: pagination.current,
    size: pagination.size,
    routerUrl: props.routerUrl,
  })
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取评论失败', 'error')
        return
      }
      flatList.value = res.data || []
      total.value = Number(res.total) || 0
      pagination.current = Number(res.current) || pagination.current
      pagination.size = Number(res.size) || pagination.size
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function handleSizeChange() {
  pagination.current = 1
  fetchList()
}

function startReply(item, parent) {
  replyTarget.value = item
  // 回复根评论：parent = 自身；回复子评论：parent = 根评论
  replyParentId.value = parent?.id ?? item.id
  form.content = ''
}

function clearReply() {
  replyTarget.value = null
  replyParentId.value = null
}

function handleSubmit() {
  if (submitting.value) return

  // 提交前再同步一次登录身份
  syncLoggedInProfile()

  formRef.value?.validate((valid) => {
    if (!valid) return

    submitting.value = true
    const payload = {
      content: form.content.trim(),
      nickname: (isLoggedIn.value ? displayNickname.value : form.nickname).trim(),
      mail: (
        isLoggedIn.value
          ? storeMail.value || `${String(userInfo.value?.username || 'user').slice(0, 48)}@hblog.local`
          : form.mail
      ).trim(),
      website: form.website.trim() || undefined,
      routerUrl: props.routerUrl,
    }

    if (replyTarget.value) {
      payload.replyCommentId = replyTarget.value.id
      payload.parentCommentId = replyParentId.value
    }

    addComment(payload)
      .then((res) => {
        if (res.success === false) {
          showMessage(res.message || '提交失败', 'error')
          return
        }
        showMessage('评论已提交，审核通过后展示')
        saveProfile()
        form.content = ''
        clearReply()
        formRef.value?.clearValidate()
      })
      .catch((err) => {
        console.error(err)
      })
      .finally(() => {
        submitting.value = false
      })
  })
}

watch(
  () => props.routerUrl,
  () => {
    pagination.current = 1
    clearReply()
    fetchList()
  },
)

// 登录 / 退出后切换表单模式
watch(
  isLoggedIn,
  (logged) => {
    if (logged) {
      syncLoggedInProfile()
      formRef.value?.clearValidate(['nickname', 'mail'])
    } else {
      loadProfile()
    }
  },
  { immediate: true },
)

onMounted(() => {
  // 有 token 但 pinia 无用户名时补拉（刷新页面后）
  if (getToken() && !userInfo.value?.username) {
    userStore.setUserInfo().finally(() => {
      loadProfile()
      fetchList()
    })
    return
  }
  loadProfile()
  fetchList()
})
</script>

<style scoped>
.comment-section {
  margin-top: 3rem;
  padding-top: 2rem;
  border-top: 1px solid #efefef;
}

.comment-header {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.comment-title {
  margin: 0;
  font-family: 'Source Serif 4', Georgia, 'Songti SC', serif;
  font-size: 1.35rem;
  font-weight: 700;
  color: #0f0f0f;
}

.comment-count {
  font-size: 0.85rem;
  color: #8a8a8a;
}

.comment-form {
  margin-bottom: 2rem;
  padding: 1.25rem 1.35rem;
  border: 1px solid #e8ebef;
  border-radius: 14px;
  background: #fafafa;
}

.reply-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.85rem;
  padding: 0.55rem 0.75rem;
  border-radius: 8px;
  background: #f0f0f0;
  font-size: 0.875rem;
  color: #4a4a4a;
}

.reply-cancel {
  border: 0;
  background: transparent;
  color: #8a8a8a;
  cursor: pointer;
  font-size: 0.8rem;
}

.reply-cancel:hover {
  color: #0f0f0f;
}

.comment-logged {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.85rem;
  padding: 0.75rem 0.9rem;
  border-radius: 10px;
  background: #f7f7f7;
  border: 1px solid #efefef;
}

.comment-logged__avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  background: #0f0f0f;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 700;
}

.comment-logged__meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.comment-logged__meta strong {
  font-size: 0.925rem;
  color: #0f0f0f;
}

.comment-logged__meta span {
  font-size: 0.75rem;
  color: #8a8a8a;
}

.comment-form__row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0 1rem;
}

.comment-form__row--single {
  max-width: 320px;
}

.comment-form__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.85rem;
  margin-top: 0.25rem;
}

.comment-hint {
  margin: 0;
  font-size: 0.8rem;
  color: #8a8a8a;
}

.comment-list {
  min-height: 80px;
}

.comment-item {
  display: flex;
  gap: 0.9rem;
  padding: 1.15rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: 0;
}

.comment-item--child {
  padding: 0.85rem 0 0;
  border-bottom: 0;
}

.comment-avatar {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: #e8e8e8;
  color: #4a4a4a;
  font-size: 0.95rem;
  font-weight: 600;
}

.comment-avatar--sm {
  width: 34px;
  height: 34px;
  font-size: 0.8rem;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  min-width: 0;
  flex: 1;
}

.comment-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.35rem 0.55rem;
  margin-bottom: 0.4rem;
}

.comment-name {
  font-size: 0.92rem;
  font-weight: 600;
  color: #0f0f0f;
}

.comment-name--link {
  text-decoration: none;
}

.comment-name--link:hover {
  text-decoration: underline;
  text-underline-offset: 2px;
}

.comment-reply-arrow {
  font-size: 0.78rem;
  color: #a3a3a3;
}

.comment-reply-name {
  font-size: 0.85rem;
  color: #6b6b6b;
}

.comment-time {
  font-size: 0.78rem;
  color: #a3a3a3;
}

.comment-content {
  margin: 0;
  font-size: 0.95rem;
  line-height: 1.7;
  color: #2a2a2a;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-reply-btn {
  margin-top: 0.45rem;
  padding: 0;
  border: 0;
  background: transparent;
  color: #8a8a8a;
  font-size: 0.8rem;
  cursor: pointer;
}

.comment-reply-btn:hover {
  color: #0f0f0f;
}

.comment-children {
  margin-top: 0.75rem;
  padding-left: 0.85rem;
  border-left: 2px solid #efefef;
}

.comment-empty {
  padding: 2.5rem 1rem;
  text-align: center;
  color: #8a8a8a;
  font-size: 0.9rem;
}

.comment-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 1.25rem;
}

@media (min-width: 720px) {
  .comment-form__row {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
