import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getBlogSettingsDetail } from '@/api/frontend/blogSettings'

export const useBlogSettingsStore = defineStore(
  'blogSettings',
  () => {
    const settings = ref({
      logo: '',
      name: '',
      author: '',
      introduction: '',
      avatar: '',
      githubHomepage: '',
      giteeHomepage: '',
      zhihuHomepage: '',
      csdnHomepage: '',
    })

    function applySettings(data = {}) {
      settings.value = {
        logo: data.logo || '',
        name: data.name || '',
        author: data.author || '',
        introduction: data.introduction || '',
        avatar: data.avatar || '',
        githubHomepage: data.githubHomepage || '',
        giteeHomepage: data.giteeHomepage || '',
        zhihuHomepage: data.zhihuHomepage || '',
        csdnHomepage: data.csdnHomepage || '',
      }
    }

    function fetchSettings() {
      return getBlogSettingsDetail()
        .then((res) => {
          if (res.success === false) return
          applySettings(res.data || {})
        })
        .catch((err) => {
          console.error('获取博客设置失败', err)
        })
    }

    return { settings, applySettings, fetchSettings }
  },
  {
    persist: true,
  },
)
