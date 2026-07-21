<template>
  <div class="page">
    <!-- 筛选区 -->
    <section class="page-card filter-card">
      <el-form :model="filter" inline class="filter-form" @submit.prevent>
        <el-form-item label="昵称">
          <el-input
            v-model="filter.nickname"
            placeholder="请输入昵称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="路由">
          <el-input
            v-model="filter.routerUrl"
            placeholder="如 /article/1"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="filter.status"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" :loading="loading" @click="handleSearch">
            查询
          </el-button>
          <el-button :icon="RefreshRight" :disabled="loading" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 表格区 -->
    <section class="page-card table-card">
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" min-width="110" />
        <el-table-column prop="mail" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="评论内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="routerUrl" label="所属路由" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="驳回原因" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 2"
              type="success"
              link
              :icon="Select"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status !== 3"
              type="warning"
              link
              :icon="CloseBold"
              @click="openRejectDialog(row)"
            >
              驳回
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </section>

    <!-- 驳回弹窗 -->
    <FormDialog
      ref="rejectDialogRef"
      v-model="rejectDialogVisible"
      title="驳回评论"
      width="480px"
      :form-model="rejectForm"
      :rules="rejectRules"
      :loading="submitLoading"
      confirm-text="确认驳回"
      @confirm="handleReject"
      @closed="resetRejectForm"
    >
      <el-form-item label="驳回原因" prop="reason">
        <el-input
          v-model="rejectForm.reason"
          type="textarea"
          :rows="4"
          maxlength="300"
          show-word-limit
          placeholder="请输入驳回原因（可选）"
        />
      </el-form-item>
    </FormDialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { CloseBold, Delete, RefreshRight, Search, Select } from '@element-plus/icons-vue'
import FormDialog from '@/components/FormDialog.vue'
import { auditComment, deleteComment, getCommentPageList } from '@/api/admin/comment'
import { showMessage, showModel } from '@/composables/util'

defineOptions({ name: 'AdminComments' })

const STATUS_MAP = {
  1: { label: '待审核', type: 'info' },
  2: { label: '已通过', type: 'success' },
  3: { label: '已驳回', type: 'danger' },
}

const statusOptions = [
  { label: '待审核', value: 1 },
  { label: '已通过', value: 2 },
  { label: '已驳回', value: 3 },
]

const loading = ref(false)
const submitLoading = ref(false)
const rejectDialogVisible = ref(false)
const rejectDialogRef = ref()
const tableData = ref([])

const filter = reactive({
  nickname: '',
  routerUrl: '',
  status: undefined,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const rejectForm = reactive({
  id: null,
  reason: '',
})

const rejectRules = {
  reason: [{ max: 300, message: '驳回原因不能超过 300 字', trigger: 'blur' }],
}

function statusLabel(status) {
  return STATUS_MAP[status]?.label || '未知'
}

function statusTagType(status) {
  return STATUS_MAP[status]?.type || 'info'
}

function buildQuery() {
  return {
    current: pagination.current,
    size: pagination.size,
    nickname: filter.nickname?.trim() || undefined,
    routerUrl: filter.routerUrl?.trim() || undefined,
    status: filter.status ?? undefined,
  }
}

function fetchList() {
  if (loading.value) return

  loading.value = true
  const startedAt = Date.now()
  const minLoadingMs = 500

  getCommentPageList(buildQuery())
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取评论列表失败', 'error')
        return
      }
      tableData.value = res.data || []
      pagination.total = Number(res.total) || 0
      pagination.current = Number(res.current) || pagination.current
      pagination.size = Number(res.size) || pagination.size
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(async () => {
      const wait = Math.max(0, minLoadingMs - (Date.now() - startedAt))
      if (wait > 0) {
        await new Promise((resolve) => setTimeout(resolve, wait))
      }
      loading.value = false
    })
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function handleReset() {
  filter.nickname = ''
  filter.routerUrl = ''
  filter.status = undefined
  pagination.current = 1
  fetchList()
}

function handleApprove(row) {
  showModel(`确认通过「${row.nickname}」的评论吗？`, 'warning', '审核确认')
    .then(() => auditComment({ id: row.id, status: 2 }))
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '审核失败', 'error')
        return
      }
      showMessage('已通过')
      fetchList()
    })
    .catch(() => {})
}

function openRejectDialog(row) {
  rejectForm.id = row.id
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

function resetRejectForm() {
  rejectForm.id = null
  rejectForm.reason = ''
  rejectDialogRef.value?.clearValidate()
}

function handleReject() {
  if (submitLoading.value || rejectForm.id == null) return

  submitLoading.value = true
  auditComment({
    id: rejectForm.id,
    status: 3,
    reason: rejectForm.reason?.trim() || undefined,
  })
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '驳回失败', 'error')
        return
      }
      showMessage('已驳回')
      rejectDialogVisible.value = false
      fetchList()
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(() => {
      submitLoading.value = false
    })
}

function handleDelete(row) {
  showModel(`确认删除「${row.nickname}」的评论吗？`, 'warning', '删除确认')
    .then(() => deleteComment({ id: row.id }))
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '删除失败', 'error')
        return
      }
      showMessage('删除成功')
      if (tableData.value.length <= 1 && pagination.current > 1) {
        pagination.current -= 1
      }
      fetchList()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-card {
  border: 1px solid #eaedf2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 6px 22px rgba(30, 41, 59, 0.035);
}

.filter-card {
  padding: 18px 18px 4px;
}

.filter-form :deep(.el-form-item) {
  margin-right: 18px;
  margin-bottom: 14px;
}

.table-card {
  padding: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
