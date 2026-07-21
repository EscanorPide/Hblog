<template>
  <!-- 四舍五入取整，避免滚动过程中出现小数 -->
  <span class="count-to">{{ formatted }}</span>
</template>

<script setup>
import gsap from 'gsap'
import { computed, onBeforeUnmount, reactive, watch } from 'vue'

defineOptions({ name: 'CountTo' })

/* 继承父级字号/字重，避免数字看起来偏小 */
const props = defineProps({
  /** 目标数值 */
  value: {
    type: Number,
    default: 0,
  },
  /** 动画时长（秒） */
  duration: {
    type: Number,
    default: 1.6,
  },
  /** 千分位分隔符，传空字符串则不分割 */
  separator: {
    type: String,
    default: ',',
  },
})

const d = reactive({
  num: 0,
})

const formatted = computed(() => {
  const intVal = Math.round(d.num)
  const text = String(intVal)
  if (!props.separator) return text
  return text.replace(/\B(?=(\d{3})+(?!\d))/g, props.separator)
})

function animateToValue() {
  gsap.killTweensOf(d)
  gsap.to(d, {
    duration: props.duration,
    num: Number(props.value) || 0,
    ease: 'power2.out',
  })
}

animateToValue()

watch(
  () => props.value,
  () => animateToValue(),
)

onBeforeUnmount(() => {
  gsap.killTweensOf(d)
})
</script>

<style scoped>
.count-to {
  display: inline-block;
  font-size: inherit;
  font-weight: inherit;
  letter-spacing: inherit;
  line-height: inherit;
  font-variant-numeric: tabular-nums;
}
</style>
