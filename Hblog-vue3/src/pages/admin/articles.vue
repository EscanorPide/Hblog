<template>
  <div class="page">
    <!-- 筛选区 -->
    <section class="page-card filter-card">
      <el-form :model="filter" inline class="filter-form" @submit.prevent>
        <el-form-item label="文章标题">
          <el-input
            v-model="filter.title"
            placeholder="请输入 (模糊查询)"
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
        <el-button type="primary" :icon="EditPen" @click="openPublishDialog">写文章</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="240" show-overflow-tooltip />
        <el-table-column label="封面" width="120" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.cover"
              :src="row.cover"
              :preview-src-list="[row.cover]"
              preview-teleported
              fit="cover"
              class="cover-thumb"
            />
            <span v-else class="cover-empty">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" min-width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
              编辑
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

    <!-- 写文章 / 编辑文章 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑文章' : '写文章'"
      width="920px"
      top="4vh"
      destroy-on-close
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        v-loading="detailLoading"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="article-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入文章标题（1~40 字）"
            maxlength="40"
            show-word-limit
            clearable
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <MdEditor
            v-model="form.content"
            language="zh-CN"
            style="height: 420px"
            :on-upload-img="onUploadImg"
          />
        </el-form-item>

        <el-form-item label="封面" prop="cover">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <img v-if="form.cover" :src="form.cover" class="cover-preview" alt="文章封面" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            placeholder="请选择文章分类"
            clearable
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="form.tags"
            multiple
            filterable
            allow-create
            default-first-option
            :reserve-keyword="false"
            placeholder="请选择或输入新标签"
            style="width: 100%"
          >
            <el-option
              v-for="item in tagOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要（选填）"
            maxlength="160"
            show-word-limit
            resize="vertical"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, EditPen, Plus, RefreshRight, Search } from '@element-plus/icons-vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import {
  deleteArticle,
  getArticleDetail,
  getArticlePageList,
  publishArticle,
  updateArticle,
} from '@/api/admin/article'
import { getCategorySelectList } from '@/api/admin/category'
import { getTagSelectList } from '@/api/admin/tag'
import { uploadFile } from '@/api/admin/file'
import { showMessage, showModel } from '@/composables/util'

defineOptions({ name: 'AdminArticles' })

const loading = ref(false)
const detailLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const tableData = ref([])
const categoryOptions = ref([])
const tagOptions = ref([])

const filter = reactive({
  title: '',
  dateRange: null,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const form = reactive({
  id: null,
  title: '',
  content: '',
  cover: '',
  categoryId: null,
  tags: [],
  summary: '',
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 40, message: '文章标题字数需在 1 ~ 40 之间', trigger: 'blur' },
  ],
  content: [{ required: true, message: '请输入文章内容', trigger: 'change' }],
  cover: [{ required: true, message: '请上传文章封面', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择文章分类', trigger: 'change' }],
  tags: [{ type: 'array', required: true, min: 1, message: '请至少选择一个标签', trigger: 'change' }],
}

function buildQuery() {
  const [startDate, endDate] = filter.dateRange || []
  return {
    current: pagination.current,
    size: pagination.size,
    title: filter.title?.trim() || undefined,
    startDate: startDate || undefined,
    endDate: endDate || undefined,
  }
}

function fetchList() {
  if (loading.value) return

  loading.value = true
  const startedAt = Date.now()
  const minLoadingMs = 500

  getArticlePageList(buildQuery())
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取文章列表失败', 'error')
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

function fetchOptions() {
  getCategorySelectList()
    .then((res) => {
      if (res.success === false) return
      categoryOptions.value = res.data || []
    })
    .catch((err) => console.error(err))

  getTagSelectList()
    .then((res) => {
      if (res.success === false) return
      tagOptions.value = res.data || []
    })
    .catch((err) => console.error(err))
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function handleReset() {
  filter.title = ''
  filter.dateRange = null
  pagination.current = 1
  fetchList()
}

function resetForm() {
  form.id = null
  form.title = ''
  form.content = ''
  form.cover = ''
  form.categoryId = null
  form.tags = []
  form.summary = ''
  formRef.value?.clearValidate()
}

function openPublishDialog() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  resetForm()
  dialogVisible.value = true
  detailLoading.value = true

  getArticleDetail({ id: row.id })
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取文章详情失败', 'error')
        dialogVisible.value = false
        return
      }
      const data = res.data || {}
      form.id = data.id
      form.title = data.title || ''
      form.content = data.content || ''
      form.cover = data.cover || ''
      form.categoryId = data.categoryId ?? null
      form.tags = (data.tagIds || []).map((id) => Number(id))
      form.summary = data.summary || ''
    })
    .catch((err) => {
      console.error(err)
      dialogVisible.value = false
    })
    .finally(() => {
      detailLoading.value = false
    })
}

function handleCoverUpload(options) {
  const file = options.file
  if (!file?.type?.startsWith('image/')) {
    showMessage('请上传图片文件', 'warning')
    return
  }

  uploadFile(file)
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '封面上传失败', 'error')
        return
      }
      form.cover = res.data?.url || ''
      formRef.value?.clearValidate('cover')
      showMessage('封面上传成功')
    })
    .catch((err) => console.error(err))
}

async function onUploadImg(files, callback) {
  const urls = []
  for (const file of files) {
    try {
      const res = await uploadFile(file)
      if (res.success === false) {
        showMessage(res.message || '图片上传失败', 'error')
        continue
      }
      if (res.data?.url) urls.push(res.data.url)
    } catch (err) {
      console.error(err)
    }
  }
  callback(urls)
}

function buildPayload() {
  return {
    title: form.title.trim(),
    content: form.content,
    cover: form.cover,
    summary: form.summary?.trim() || undefined,
    categoryId: form.categoryId,
    // 已有标签传 ID 字符串，新建标签传名称
    tags: (form.tags || []).map((tag) => String(tag)),
  }
}

function handleSubmit() {
  if (submitLoading.value) return

  formRef.value?.validate((valid) => {
    if (!valid) return

    submitLoading.value = true
    const payload = buildPayload()
    const request = isEdit.value
      ? updateArticle({ ...payload, id: form.id })
      : publishArticle(payload)

    request
      .then((res) => {
        if (res.success === false) {
          showMessage(res.message || (isEdit.value ? '保存失败' : '发布失败'), 'error')
          return
        }
        showMessage(isEdit.value ? '保存成功' : '发布成功')
        dialogVisible.value = false
        if (!isEdit.value) pagination.current = 1
        fetchList()
        fetchOptions()
      })
      .catch((err) => console.error(err))
      .finally(() => {
        submitLoading.value = false
      })
  })
}

function handleDelete(row) {
  showModel(`确认删除文章「${row.title}」吗？`, 'warning', '删除确认')
    .then(() => deleteArticle({ id: row.id }))
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
  fetchOptions()
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

.cover-thumb {
  width: 60px;
  height: 40px;
  border-radius: 4px;
}

.cover-empty {
  color: #c0c4cc;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.article-form {
  padding-right: 8px;
}

.cover-uploader :deep(.el-upload) {
  width: 178px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.cover-uploader-icon {
  color: #8c939d;
  font-size: 28px;
}

.cover-preview {
  width: 178px;
  height: 120px;
  display: block;
  object-fit: cover;
}
</style>
