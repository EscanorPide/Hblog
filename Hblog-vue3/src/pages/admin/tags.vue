<template>
  <div class="page">
    <!-- 筛选区 -->
    <section class="page-card filter-card">
      <el-form :model="filter" inline class="filter-form" @submit.prevent>
        <el-form-item label="标签名称">
          <el-input
            v-model="filter.name"
            placeholder="请输入标签名称"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="创建日期">
          <el-date-picker
            v-model="filter.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
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
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="openAddDialog">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="name" label="标签名称" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
              修改
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

    <!-- 新增弹窗 -->
    <FormDialog
      ref="addDialogRef"
      v-model="dialogVisible"
      title="新增标签"
      :form-model="addForm"
      :rules="addRules"
      :loading="submitLoading"
      @confirm="handleAdd"
      @closed="resetAddForm"
    >
      <el-form-item label="标签名称" prop="name">
        <el-input
          v-model="addForm.name"
          placeholder="请输入标签名称（1~60 字）"
          maxlength="60"
          show-word-limit
          clearable
          @keyup.enter="addDialogRef?.submit()"
        />
      </el-form-item>
    </FormDialog>

    <!-- 修改弹窗 -->
    <FormDialog
      ref="editDialogRef"
      v-model="editDialogVisible"
      title="修改标签"
      :form-model="editForm"
      :rules="addRules"
      :loading="submitLoading"
      @confirm="handleEdit"
      @closed="resetEditForm"
    >
      <el-form-item label="标签名称" prop="name">
        <el-input
          v-model="editForm.name"
          placeholder="请输入标签名称（1~60 字）"
          maxlength="60"
          show-word-limit
          clearable
          @keyup.enter="editDialogRef?.submit()"
        />
      </el-form-item>
    </FormDialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus, RefreshRight, Search } from '@element-plus/icons-vue'
import FormDialog from '@/components/FormDialog.vue'
import { addTag, deleteTag, getTagPageList, updateTag } from '@/api/admin/tag'
import { showMessage, showModel } from '@/composables/util'

defineOptions({ name: 'AdminTags' })

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const addDialogRef = ref()
const editDialogVisible = ref(false)
const editDialogRef = ref()
const tableData = ref([])

const filter = reactive({
  name: '',
  dateRange: null,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const addForm = reactive({
  name: '',
})

const editForm = reactive({
  id: null,
  name: '',
})

const addRules = {
  name: [
    { required: true, message: '标签名称不能为空', trigger: 'blur' },
    { min: 1, max: 60, message: '标签名称字数限制 1 ~ 60 之间', trigger: 'blur' },
  ],
}

function buildQuery() {
  const [startDate, endDate] = filter.dateRange || []
  return {
    current: pagination.current,
    size: pagination.size,
    name: filter.name?.trim() || undefined,
    startDate: startDate || undefined,
    endDate: endDate || undefined,
  }
}

function fetchList() {
  if (loading.value) return

  loading.value = true
  const startedAt = Date.now()
  const minLoadingMs = 500

  getTagPageList(buildQuery())
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取标签列表失败', 'error')
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
  filter.name = ''
  filter.dateRange = null
  pagination.current = 1
  fetchList()
}

function openAddDialog() {
  dialogVisible.value = true
}

function resetAddForm() {
  addForm.name = ''
  addDialogRef.value?.clearValidate()
}

function openEditDialog(row) {
  editForm.id = row.id
  editForm.name = row.name
  editDialogVisible.value = true
}

function resetEditForm() {
  editForm.id = null
  editForm.name = ''
  editDialogRef.value?.clearValidate()
}

function handleAdd() {
  if (submitLoading.value) return

  submitLoading.value = true
  addTag({ name: addForm.name.trim() })
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '新增标签失败', 'error')
        return
      }
      showMessage('新增成功')
      dialogVisible.value = false
      pagination.current = 1
      fetchList()
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(() => {
      submitLoading.value = false
    })
}

function handleEdit() {
  if (submitLoading.value || editForm.id == null) return

  submitLoading.value = true
  updateTag({ id: editForm.id, name: editForm.name.trim() })
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '修改标签失败', 'error')
        return
      }
      showMessage('修改成功')
      editDialogVisible.value = false
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
  showModel(`确认删除标签「${row.name}」吗？`, 'warning', '删除确认')
    .then(() => {
      return deleteTag({ id: row.id })
    })
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

.table-toolbar {
  margin-bottom: 14px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
