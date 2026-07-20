<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    :width="width"
    append-to-body
    destroy-on-close
    :close-on-click-modal="closeOnClickModal"
    @update:model-value="emit('update:modelValue', $event)"
    @closed="emit('closed')"
  >
    <el-form
      ref="formRef"
      :model="formModel"
      :rules="rules"
      :label-width="labelWidth"
      @submit.prevent="submit"
    >
      <slot />
    </el-form>

    <template #footer>
      <slot name="footer" :loading="loading" :submit="submit" :cancel="cancel">
        <el-button @click="cancel">{{ cancelText }}</el-button>
        <el-button type="primary" :loading="loading" @click="submit">
          {{ confirmText }}
        </el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  width: {
    type: [String, Number],
    default: '420px',
  },
  labelWidth: {
    type: String,
    default: '88px',
  },
  /** 表单数据对象 */
  formModel: {
    type: Object,
    required: true,
  },
  /** 表单校验规则 */
  rules: {
    type: Object,
    default: () => ({}),
  },
  /** 确认按钮 loading */
  loading: {
    type: Boolean,
    default: false,
  },
  confirmText: {
    type: String,
    default: '确定',
  },
  cancelText: {
    type: String,
    default: '取消',
  },
  closeOnClickModal: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue', 'confirm', 'closed', 'cancel'])

const formRef = ref()

function cancel() {
  emit('update:modelValue', false)
  emit('cancel')
}

/** 校验通过后触发 confirm，供按钮 / 回车调用 */
async function submit() {
  if (!formRef.value || props.loading) return

  await formRef.value.validate((valid) => {
    if (!valid) return
    emit('confirm')
  })
}

function clearValidate() {
  formRef.value?.clearValidate()
}

function resetFields() {
  formRef.value?.resetFields()
}

defineExpose({
  submit,
  cancel,
  clearValidate,
  resetFields,
  formRef,
})
</script>
