import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useMenuStore = defineStore(
  'menu',
  () => {
    // 菜单是否折叠：false 展开，true 收起（小屏默认收起）
    const menuCollapsed = ref(typeof window !== 'undefined' ? window.innerWidth <= 800 : false)

    // 切换菜单折叠状态
    function toggleMenu() {
      menuCollapsed.value = !menuCollapsed.value
    }

    // 设置菜单折叠状态
    function setMenuCollapsed(collapsed) {
      menuCollapsed.value = collapsed
    }

    return { menuCollapsed, toggleMenu, setMenuCollapsed }
  },
  {
    // 开启持久化（localStorage）
    persist: true,
  },
)
