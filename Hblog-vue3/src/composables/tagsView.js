import { computed, reactive } from 'vue'

const HOME_PATH = '/admin/index'

const homeTag = {
  path: HOME_PATH,
  title: '后台首页',
  name: 'AdminIndex',
  affix: true,
}

const state = reactive({
  tags: [{ ...homeTag }],
})

function isAdminLeafRoute(route) {
  if (!route?.path?.startsWith('/admin')) return false
  return route.matched.some((record) => record.components?.default && record.path !== '/admin')
}

function resolveName(route) {
  return route.name || route.matched[route.matched.length - 1]?.name || ''
}

function resolveTitle(route) {
  return route.meta?.title || '未命名页面'
}

export function useTagsView() {
  const tags = state.tags
  const cachedNames = computed(() =>
    tags.map((tag) => tag.name).filter((name) => typeof name === 'string' && name.length > 0),
  )

  function addOrSyncTag(route) {
    if (!isAdminLeafRoute(route)) return

    const path = route.path
    const title = resolveTitle(route)
    const name = resolveName(route)
    const affix = Boolean(route.meta?.affix) || path === HOME_PATH
    const existing = tags.find((tag) => tag.path === path)

    if (existing) {
      existing.title = title
      existing.name = name || existing.name
      existing.affix = existing.affix || affix
      return
    }

    tags.push({ path, title, name, affix })
  }

  function removeTag(path) {
    const index = tags.findIndex((tag) => tag.path === path)
    if (index === -1 || tags[index].affix) return null

    const [removed] = tags.splice(index, 1)
    return { removed, index }
  }

  function keepOnly(paths) {
    const pathSet = new Set(paths)
    const next = tags.filter((tag) => tag.affix || pathSet.has(tag.path))
    tags.splice(0, tags.length, ...next)
  }

  function resetToAffixed() {
    const affixed = tags.filter((tag) => tag.affix)
    tags.splice(0, tags.length, ...(affixed.length ? affixed : [{ ...homeTag }]))
  }

  function isAffixPath(path) {
    return tags.some((tag) => tag.path === path && tag.affix)
  }

  return {
    HOME_PATH,
    homeTag,
    tags,
    cachedNames,
    addOrSyncTag,
    removeTag,
    keepOnly,
    resetToAffixed,
    isAffixPath,
    isAdminLeafRoute,
  }
}
