<template>
  <div class="login-page" :class="{ light: isLight }">
    <!-- 流动背景：底图缓慢漂移 + 三条流光 -->
    <div class="bg-image" aria-hidden="true"></div>
    <div class="flow flow-1" aria-hidden="true"></div>
    <div class="flow flow-2" aria-hidden="true"></div>
    <div class="flow flow-3" aria-hidden="true"></div>

    <!-- 主题切换按钮 -->
    <button
      class="theme-toggle"
      type="button"
      :title="isLight ? '切换到暗色模式' : '切换到亮色模式'"
      @click="toggleTheme"
    >
      <el-icon :size="18">
        <Moon v-if="isLight" />
        <Sunny v-else />
      </el-icon>
    </button>

    <!-- 登录 / 注册卡片 -->
    <div class="login-card">
      <h1>{{ isRegister ? 'Create Account' : 'Hello, Hblog!' }}</h1>
      <p class="subtitle">
        {{ isRegister ? '注册账号，开始查看他的博客。' : '登录以访问他的博客。' }}
      </p>

      <el-form
        ref="formRef"
        :model="form"
        :rules="activeRules"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            autocomplete="username"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
            autocomplete="current-password"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item v-if="isRegister" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
            autocomplete="new-password"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div v-if="!isRegister" class="form-row">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
          <a class="forgot" href="#" @click.prevent>忘记密码？</a>
        </div>
        <div v-else class="form-row form-row--hint">
          <span class="hint">密码至少 8 位</span>
        </div>

        <el-button
          class="login-btn"
          native-type="submit"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ isRegister ? '注 册' : '登 录' }}
        </el-button>

        <p class="switch-mode">
          <template v-if="isRegister">
            已有账号？
            <a href="#" @click.prevent="switchMode(false)">去登录</a>
          </template>
          <template v-else>
            还没有账号？
            <a href="#" @click.prevent="switchMode(true)">立即注册</a>
          </template>
        </p>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Sunny, Moon } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/admin/user'
import { setToken } from '@/composables/auth'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const isRegister = ref(false)

/* 主题：默认跟随系统，可通过按钮手动切换 */
const isLight = ref(window.matchMedia('(prefers-color-scheme: light)').matches)

function toggleTheme() {
  isLight.value = !isLight.value
}

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  remember: false,
})

const validateConfirm = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 60, message: '用户名长度为 3-60 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 72, message: '密码长度为 8-72 个字符', trigger: 'blur' },
  ],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }],
}

const activeRules = computed(() => (isRegister.value ? registerRules : loginRules))

function switchMode(registerMode) {
  isRegister.value = registerMode
  form.confirmPassword = ''
  nextTick(() => formRef.value?.clearValidate())
}

function goAfterAuth() {
  const redirect = router.currentRoute.value.query.redirect
  const canEnterAdmin = userStore.hasRole('admin', 'editor')
  if (!canEnterAdmin) {
    router.push('/')
    return
  }
  router.push(typeof redirect === 'string' ? redirect : '/admin/index')
}

function doLogin() {
  return login(form.username.trim(), form.password).then((res) => {
    if (res.success === false) {
      ElMessage.error(res.message || '登录失败，请检查用户名和密码')
      return
    }
    const token = res.data?.token
    if (!token) {
      ElMessage.error('登录成功但未返回 token')
      return
    }
    setToken(token)
    return userStore.setUserInfo().then(() => {
      ElMessage.success('登录成功')
      goAfterAuth()
    })
  })
}

async function handleSubmit() {
  if (!formRef.value || loading.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return
    loading.value = true

    const task = isRegister.value
      ? register({
          username: form.username.trim(),
          password: form.password,
          confirmPassword: form.confirmPassword,
        }).then((res) => {
          if (res.success === false) {
            ElMessage.error(res.message || '注册失败')
            return
          }
          ElMessage.success('注册成功，正在登录…')
          return doLogin()
        })
      : doLogin()

    task
      .catch((err) => {
        console.error(err)
        if (err.response) {
          ElMessage.error(
            err.response.data?.message || (isRegister.value ? '注册失败' : '登录失败，请检查用户名和密码'),
          )
        } else {
          ElMessage.error(err.message || '请求出错')
        }
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
  background: #05060a;
}

/* 底图层：缓慢漂移 + 呼吸缩放，让背景整体“活”起来 */
.bg-image {
  position: absolute;
  inset: -6%;
  background: url('@/assets/images/bg/blackbg.png') center / cover no-repeat;
  animation: bg-drift 14s ease-in-out infinite alternate;
  will-change: transform;
}

@keyframes bg-drift {
  0% {
    transform: translate3d(-1.6%, -1%, 0) scale(1.06);
  }
  50% {
    transform: translate3d(1.2%, 1.4%, 0) scale(1.1);
  }
  100% {
    transform: translate3d(-0.6%, 1%, 0) scale(1.07);
  }
}

/* 流光层：无缝循环滑动的彩色光带 */
.flow {
  position: absolute;
  left: -20vw;
  width: 140vw;
  height: 32vh;
  filter: blur(64px);
  pointer-events: none;
  mix-blend-mode: screen;
  background-repeat: repeat-x;
  background-size: 200% 100%;
  animation: flow-slide linear infinite;
  will-change: background-position;
}

.flow-1 {
  top: 8%;
  transform: rotate(-14deg);
  opacity: 0.5;
  background-image: linear-gradient(
    100deg,
    transparent 0%,
    #1d3aa8 20%,
    #6d5df1 40%,
    #cfd6e4 52%,
    #7b3ff2 66%,
    transparent 100%
  );
  animation-duration: 7s;
}

.flow-2 {
  top: 42%;
  transform: rotate(7deg);
  opacity: 0.35;
  background-image: linear-gradient(
    95deg,
    transparent 0%,
    #10265c 18%,
    #3f6df0 38%,
    #b9c2d8 55%,
    #4426a8 75%,
    transparent 100%
  );
  animation-duration: 11s;
  animation-direction: reverse;
}

.flow-3 {
  bottom: -6%;
  transform: rotate(-9deg);
  opacity: 0.28;
  background-image: linear-gradient(
    90deg,
    transparent 0%,
    #23124f 25%,
    #8a5cf6 50%,
    #d8dee9 62%,
    transparent 100%
  );
  animation-duration: 14s;
}

@keyframes flow-slide {
  from {
    background-position: 0% 50%;
  }
  to {
    background-position: 200% 50%;
  }
}

/* 用户偏好减少动效时关闭动画 */
@media (prefers-reduced-motion: reduce) {
  .bg-image,
  .flow {
    animation: none;
  }
}

/* 毛玻璃卡片 */
.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: 40px 36px 34px;
  border-radius: 20px;
  background: rgba(8, 9, 14, 0.66);
  border: 1px solid rgba(255, 255, 255, 0.09);
  backdrop-filter: blur(26px);
  -webkit-backdrop-filter: blur(26px);
  box-shadow:
    0 24px 70px rgba(0, 0, 0, 0.65),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  transition:
    background 0.35s ease,
    border-color 0.35s ease,
    box-shadow 0.35s ease;
}

.login-card h1 {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.01em;
}

.subtitle {
  margin: 0 0 26px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}

/* Element Plus 输入框暗色化 */
:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.14);
  border-radius: 10px;
  padding: 2px 12px;
  transition: box-shadow 0.2s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.28);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 1.5px rgba(255, 255, 255, 0.75);
}

:deep(.el-input__inner) {
  color: #fff;
  caret-color: #fff;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.38);
}

:deep(.el-input__prefix),
:deep(.el-input__suffix) {
  color: rgba(255, 255, 255, 0.45);
}

:deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: inset 0 0 0 1px rgba(245, 108, 108, 0.85);
}

/* 记住我 / 忘记密码 */
.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 18px;
}

:deep(.el-checkbox__label) {
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}

:deep(.el-checkbox__inner) {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.3);
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: #fff;
  border-color: #fff;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #111;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #fff;
}

.forgot {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  text-decoration: underline;
  text-underline-offset: 3px;
}

.forgot:hover {
  color: #fff;
}

.form-row--hint {
  justify-content: flex-start;
  margin-bottom: 14px;
}

.hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
}

.switch-mode {
  margin: 16px 0 0;
  text-align: center;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}

.switch-mode a {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: underline;
  text-underline-offset: 3px;
}

.switch-mode a:hover {
  color: #fff;
}

/* 白色登录按钮 */
.login-btn {
  width: 100%;
  height: 42px;
  border: none;
  border-radius: 10px;
  background: #fff;
  color: #16181d;
  font-weight: 600;
  font-size: 15px;
  transition: background 0.2s;
}

.login-btn:hover,
.login-btn:focus {
  background: #e6e8ec;
  color: #16181d;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px 28px;
  }
}

/* 主题切换按钮 */
.theme-toggle {
  position: absolute;
  top: 24px;
  right: 24px;
  z-index: 2;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition:
    background 0.25s,
    border-color 0.25s,
    color 0.25s,
    transform 0.15s;
}

.theme-toggle:hover {
  background: rgba(255, 255, 255, 0.18);
  transform: scale(1.06);
}

.theme-toggle:active {
  transform: scale(0.96);
}

/* ============ 亮色模式（.light 类驱动） ============ */
.login-page.light {
  background: #f4f5fa;
}

.login-page.light .bg-image {
  background-image: url('@/assets/images/bg/whitebg.png');
}

/* 亮色下改用柔和淡彩流光（screen 混合在白底上会看不见） */
.login-page.light .flow {
  mix-blend-mode: normal;
}

.login-page.light .flow-1 {
  opacity: 0.3;
  background-image: linear-gradient(
    100deg,
    transparent 0%,
    #b7c6f5 22%,
    #d3c7fa 42%,
    #f0f3fa 52%,
    #c9b6f7 66%,
    transparent 100%
  );
}

.login-page.light .flow-2 {
  opacity: 0.24;
  background-image: linear-gradient(
    95deg,
    transparent 0%,
    #c4d2f2 18%,
    #a9c0f5 38%,
    #eef1f7 55%,
    #cbc0f2 75%,
    transparent 100%
  );
}

.login-page.light .flow-3 {
  opacity: 0.2;
  background-image: linear-gradient(
    90deg,
    transparent 0%,
    #d5c9f5 25%,
    #bfaef2 50%,
    #f2f4f8 62%,
    transparent 100%
  );
}

.login-page.light .theme-toggle {
  border-color: rgba(0, 0, 0, 0.12);
  background: rgba(255, 255, 255, 0.6);
  color: rgba(0, 0, 0, 0.7);
}

.login-page.light .theme-toggle:hover {
  background: rgba(255, 255, 255, 0.85);
}

/* 毛玻璃卡片 */
.login-page.light .login-card {
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow:
    0 24px 70px rgba(80, 90, 130, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.login-page.light .login-card h1 {
  color: #16181d;
}

.login-page.light .subtitle {
  color: rgba(0, 0, 0, 0.5);
}

/* 输入框亮色化 */
.login-page.light :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.6);
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.14);
}

.login-page.light :deep(.el-input__wrapper:hover) {
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.28);
}

.login-page.light :deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 1.5px rgba(0, 0, 0, 0.75);
}

.login-page.light :deep(.el-input__inner) {
  color: #16181d;
  caret-color: #16181d;
}

.login-page.light :deep(.el-input__inner::placeholder) {
  color: rgba(0, 0, 0, 0.35);
}

.login-page.light :deep(.el-input__prefix),
.login-page.light :deep(.el-input__suffix) {
  color: rgba(0, 0, 0, 0.4);
}

.login-page.light :deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: inset 0 0 0 1px rgba(245, 108, 108, 0.85);
}

/* 记住我 / 忘记密码 */
.login-page.light :deep(.el-checkbox__label) {
  color: rgba(0, 0, 0, 0.65);
}

.login-page.light :deep(.el-checkbox__inner) {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(0, 0, 0, 0.3);
}

.login-page.light :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: #16181d;
  border-color: #16181d;
}

.login-page.light :deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #fff;
}

.login-page.light :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #16181d;
}

.login-page.light .forgot {
  color: rgba(0, 0, 0, 0.6);
}

.login-page.light .forgot:hover {
  color: #000;
}

.login-page.light .hint {
  color: rgba(0, 0, 0, 0.4);
}

.login-page.light .switch-mode {
  color: rgba(0, 0, 0, 0.5);
}

.login-page.light .switch-mode a {
  color: #16181d;
}

.login-page.light .switch-mode a:hover {
  color: #000;
}

/* 黑色登录按钮 */
.login-page.light .login-btn {
  background: #16181d;
  color: #fff;
}

.login-page.light .login-btn:hover,
.login-page.light .login-btn:focus {
  background: #2b2f38;
  color: #fff;
}
</style>
