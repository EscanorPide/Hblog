<template>
  <FormDialog
    ref="dialogRef"
    :model-value="modelValue"
    title="修改密码"
    :form-model="form"
    :rules="rules"
    :loading="loading"
    @update:model-value="emit('update:modelValue', $event)"
    @confirm="handleSubmit"
    @closed="resetForm"
  >
    <el-form-item label="用户名" prop="username">
      <el-input v-model="form.username" disabled />
    </el-form-item>
    <el-form-item label="新密码" prop="password">
      <el-input
        v-model="form.password"
        type="password"
        placeholder="请输入新密码"
        show-password
        clearable
      />
    </el-form-item>
    <el-form-item label="确认密码" prop="rePassword">
      <el-input
        v-model="form.rePassword"
        type="password"
        placeholder="请再次输入新密码"
        show-password
        clearable
        @keyup.enter="dialogRef?.submit()"
      />
    </el-form-item>
  </FormDialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import FormDialog from '@/components/FormDialog.vue'
import { updateAdminPassword } from '@/api/admin/user'
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const dialogRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  rePassword: '',
})

const validateRePassword = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  rePassword: [{ required: true, validator: validateRePassword, trigger: 'blur' }],
}

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      form.username = userStore.userInfo?.username || ''
      form.password = ''
      form.rePassword = ''
    }
  },
)

function resetForm() {
  form.password = ''
  form.rePassword = ''
  dialogRef.value?.clearValidate()
}

function handleSubmit() {
  if (loading.value) return

  loading.value = true
  updateAdminPassword(form.username, form.password)
    .then((res) => {
      if (res.success === false) {
        showMessage(res.message || '修改密码失败', 'error')
        return
      }
      showMessage('密码修改成功')
      emit('update:modelValue', false)
    })
    .catch((err) => {
      console.error(err)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
