<script setup>
import { onBeforeUnmount, watch } from 'vue'
import { SwitchButton } from '@element-plus/icons-vue'

defineOptions({ name: 'LogoutConfirm' })

const visible = defineModel({ type: Boolean, default: false })
const emit = defineEmits(['confirm'])

watch(visible, (open) => {
  if (open) {
    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', onKeydown)
  } else {
    document.body.style.overflow = ''
    window.removeEventListener('keydown', onKeydown)
  }
})

onBeforeUnmount(() => {
  document.body.style.overflow = ''
  window.removeEventListener('keydown', onKeydown)
})

function close() {
  visible.value = false
}

function onKeydown(e) {
  if (e.key === 'Escape') {
    e.preventDefault()
    close()
  }
}

function confirm() {
  close()
  emit('confirm')
}
</script>

<template>
  <Teleport to="body">
    <Transition name="logout-fade">
      <div v-if="visible" class="logout-overlay" @click.self="close">
        <div
          class="logout-modal"
          role="alertdialog"
          aria-modal="true"
          aria-label="退出登录确认"
        >
          <div class="logout-modal__icon" aria-hidden="true">
            <el-icon :size="22"><SwitchButton /></el-icon>
          </div>
          <h3 class="logout-modal__title">退出登录</h3>
          <p class="logout-modal__text">确定要退出当前账号吗？退出后需要重新登录。</p>
          <div class="logout-modal__actions">
            <button type="button" class="logout-btn logout-btn--ghost" @click="close">
              取消
            </button>
            <button type="button" class="logout-btn logout-btn--solid" @click="confirm">
              退出登录
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.logout-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
}

.logout-modal {
  width: min(360px, 100%);
  padding: 1.9rem 1.6rem 1.5rem;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.18);
  text-align: center;
}

.logout-modal__icon {
  width: 52px;
  height: 52px;
  margin: 0 auto;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #fef2f2;
  color: #dc2626;
}

.logout-modal__title {
  margin: 1rem 0 0;
  font-size: 1.05rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: #0f0f0f;
}

.logout-modal__text {
  margin: 0.55rem 0 0;
  font-size: 0.875rem;
  line-height: 1.7;
  color: #6b6b6b;
}

.logout-modal__actions {
  display: flex;
  gap: 0.65rem;
  margin-top: 1.5rem;
}

.logout-btn {
  flex: 1;
  height: 40px;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition:
    background 0.15s ease,
    color 0.15s ease,
    border-color 0.15s ease;
}

.logout-btn--ghost {
  border: 1px solid #e5e5e5;
  background: #fff;
  color: #3a3a3a;
}

.logout-btn--ghost:hover {
  border-color: #d4d4d4;
  background: #f7f7f7;
  color: #0f0f0f;
}

.logout-btn--solid {
  border: 1px solid #0f0f0f;
  background: #0f0f0f;
  color: #fff;
}

.logout-btn--solid:hover {
  background: #2a2a2a;
  border-color: #2a2a2a;
}

.logout-fade-enter-active,
.logout-fade-leave-active {
  transition: opacity 0.18s ease;
}

.logout-fade-enter-active .logout-modal,
.logout-fade-leave-active .logout-modal {
  transition:
    transform 0.18s ease,
    opacity 0.18s ease;
}

.logout-fade-enter-from,
.logout-fade-leave-to {
  opacity: 0;
}

.logout-fade-enter-from .logout-modal,
.logout-fade-leave-to .logout-modal {
  opacity: 0;
  transform: translateY(8px) scale(0.96);
}
</style>
