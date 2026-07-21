<template>
  <div class="page">
    <section class="page-card" v-loading="pageLoading">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="160px"
        class="settings-form"
        @submit.prevent
      >
        <el-form-item label="博客名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入博客名称"
            clearable
            maxlength="20"
            show-word-limit
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="作者名" prop="author">
          <el-input
            v-model="form.author"
            placeholder="请输入作者名"
            clearable
            maxlength="20"
            show-word-limit
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="博客 LOGO" prop="logo">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="(options) => handleUpload(options, 'logo')"
            accept="image/*"
          >
            <img v-if="form.logo" :src="form.logo" class="avatar-preview" alt="博客 LOGO" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="作者头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="(options) => handleUpload(options, 'avatar')"
            accept="image/*"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar-preview" alt="作者头像" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="介绍语" prop="introduction">
          <el-input
            v-model="form.introduction"
            type="textarea"
            :rows="4"
            placeholder="请输入介绍语"
            maxlength="120"
            show-word-limit
            resize="vertical"
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="开启 GitHub 访问">
          <el-switch v-model="social.github" />
        </el-form-item>
        <el-form-item v-if="social.github" label="GitHub 主页访问地址" prop="githubHomepage">
          <el-input
            v-model="form.githubHomepage"
            placeholder="请输入 GitHub 主页地址"
            clearable
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="开启 Gitee 访问">
          <el-switch v-model="social.gitee" />
        </el-form-item>
        <el-form-item v-if="social.gitee" label="Gitee 主页访问地址" prop="giteeHomepage">
          <el-input
            v-model="form.giteeHomepage"
            placeholder="请输入 Gitee 主页地址"
            clearable
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="开启知乎访问">
          <el-switch v-model="social.zhihu" />
        </el-form-item>
        <el-form-item v-if="social.zhihu" label="知乎主页访问地址" prop="zhihuHomepage">
          <el-input
            v-model="form.zhihuHomepage"
            placeholder="请输入知乎主页地址"
            clearable
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item label="开启 CSDN 访问">
          <el-switch v-model="social.csdn" />
        </el-form-item>
        <el-form-item v-if="social.csdn" label="CSDN 主页访问地址" prop="csdnHomepage">
          <el-input
            v-model="form.csdnHomepage"
            placeholder="请输入 CSDN 主页地址"
            clearable
            style="width: 420px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            保存
          </el-button>
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getBlogSettingsDetail, updateBlogSettings } from '@/api/admin/blogSettings'
import { resolveUploadUrl, uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogSettings'

defineOptions({ name: 'AdminSettings' })

const formRef = ref()
const pageLoading = ref(false)
const submitLoading = ref(false)
const blogSettingsStore = useBlogSettingsStore()

const form = reactive({
  name: '',
  author: '',
  logo: '',
  avatar: '',
  introduction: '',
  githubHomepage: '',
  giteeHomepage: '',
  zhihuHomepage: '',
  csdnHomepage: '',
})

const social = reactive({
  github: false,
  gitee: false,
  zhihu: false,
  csdn: false,
})

function hasUrl(value) {
  return Boolean(value && String(value).trim())
}

function fillForm(data = {}) {
  form.name = data.name || ''
  form.author = data.author || ''
  form.logo = data.logo || ''
  form.avatar = data.avatar || ''
  form.introduction = data.introduction || ''
  form.githubHomepage = data.githubHomepage || ''
  form.giteeHomepage = data.giteeHomepage || ''
  form.zhihuHomepage = data.zhihuHomepage || ''
  form.csdnHomepage = data.csdnHomepage || ''

  social.github = hasUrl(form.githubHomepage)
  social.gitee = hasUrl(form.giteeHomepage)
  social.zhihu = hasUrl(form.zhihuHomepage)
  social.csdn = hasUrl(form.csdnHomepage)
}

function fetchDetail() {
  pageLoading.value = true
  getBlogSettingsDetail()
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '获取设置失败', 'error')
        return
      }
      fillForm(res.data || {})
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(() => {
      pageLoading.value = false
    })
}

const urlRule = {
  type: 'url',
  message: '请输入合法的网址（需包含 http/https）',
  trigger: 'blur',
}

const rules = {
  name: [{ required: true, message: '博客名称不能为空', trigger: 'blur' }],
  author: [{ required: true, message: '作者名不能为空', trigger: 'blur' }],
  logo: [{ required: true, message: '博客 LOGO 不能为空', trigger: 'change' }],
  avatar: [{ required: true, message: '作者头像不能为空', trigger: 'change' }],
  introduction: [{ required: true, message: '介绍语不能为空', trigger: 'blur' }],
  githubHomepage: [
    { required: true, message: 'GitHub 主页地址不能为空', trigger: 'blur' },
    urlRule,
  ],
  giteeHomepage: [
    { required: true, message: 'Gitee 主页地址不能为空', trigger: 'blur' },
    urlRule,
  ],
  zhihuHomepage: [
    { required: true, message: '知乎主页地址不能为空', trigger: 'blur' },
    urlRule,
  ],
  csdnHomepage: [
    { required: true, message: 'CSDN 主页地址不能为空', trigger: 'blur' },
    urlRule,
  ],
}

function handleUpload(options, field) {
  const file = options.file
  if (!file?.type?.startsWith('image/')) {
    showMessage('请上传图片文件', 'warning')
    options?.onError?.(new Error('invalid file type'))
    return
  }

  // 先本地预览，上传成功后再替换为正式 URL
  const localUrl = URL.createObjectURL(file)
  const prevUrl = form[field]
  form[field] = localUrl
  pageLoading.value = true

  uploadFile(file)
    .then((res) => {
      if (res.success === false) {
        form[field] = prevUrl
        showMessage(res.message || '上传失败', 'error')
        options?.onError?.(new Error(res.message || 'upload failed'))
        return
      }
      const url = resolveUploadUrl(res)
      if (!url) {
        form[field] = prevUrl
        showMessage('上传成功，但未返回访问地址', 'error')
        options?.onError?.(new Error('empty upload url'))
        return
      }
      form[field] = url
      formRef.value?.clearValidate(field)
      showMessage('上传成功')
      options?.onSuccess?.(res)
    })
    .catch((err) => {
      form[field] = prevUrl
      console.error(err)
      options?.onError?.(err)
    })
    .finally(() => {
      URL.revokeObjectURL(localUrl)
      pageLoading.value = false
    })
}

function buildPayload() {
  return {
    name: form.name.trim(),
    author: form.author.trim(),
    logo: form.logo,
    avatar: form.avatar,
    introduction: form.introduction.trim(),
    githubHomepage: social.github ? form.githubHomepage.trim() : '',
    giteeHomepage: social.gitee ? form.giteeHomepage.trim() : '',
    zhihuHomepage: social.zhihu ? form.zhihuHomepage.trim() : '',
    csdnHomepage: social.csdn ? form.csdnHomepage.trim() : '',
  }
}

function handleSubmit() {
  if (submitLoading.value) return

  formRef.value?.validate((valid) => {
    if (!valid) return

    submitLoading.value = true
    updateBlogSettings(buildPayload())
      .then((res) => {
        if (res.success === false) {
          showMessage(res.message || '保存失败', 'error')
          return
        }
        blogSettingsStore.applySettings(buildPayload())
        showMessage('保存成功')
      })
      .catch((err) => {
        console.error(err)
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-card {
  padding: 28px 24px 12px;
  border: 1px solid #eaedf2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 6px 22px rgba(30, 41, 59, 0.035);
}

.settings-form {
  max-width: 720px;
}

.settings-form :deep(.el-form-item__label) {
  color: #606266;
}

.avatar-uploader :deep(.el-upload) {
  width: 120px;
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

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.avatar-uploader-icon {
  color: #8c939d;
  font-size: 28px;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}
</style>
