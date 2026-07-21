<script setup>
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useBlogSettingsStore } from '@/stores/blogSettings'

const { settings } = storeToRefs(useBlogSettingsStore())
const blogName = computed(() => settings.value.name || 'Hblog')
const year = new Date().getFullYear()
</script>

<template>
  <footer class="craft-footer">
    <div class="craft-footer__inner">
      <p>© {{ year }} {{ blogName }}</p>
      <p v-if="settings.author" class="craft-footer__author">{{ settings.author }}</p>
    </div>
  </footer>
</template>

<style scoped>
.craft-footer {
  margin-top: auto;
  border-top: 1px solid #efefef;
  background: #fff;
}

.craft-footer__inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 1.5rem 1.5rem 2rem;
  text-align: center;
  font-size: 0.85rem;
  color: #8a8a8a;
}

@media (min-width: 768px) {
  .craft-footer__inner {
    padding-left: 2rem;
    padding-right: 2rem;
  }
}

.craft-footer__author {
  margin-top: 0.35rem;
}
</style>
