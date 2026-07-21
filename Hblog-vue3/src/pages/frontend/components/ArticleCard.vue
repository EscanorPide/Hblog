<script setup>
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useBlogSettingsStore } from '@/stores/blogSettings'

defineProps({
  article: {
    type: Object,
    required: true,
  },
})

const { settings } = storeToRefs(useBlogSettingsStore())

const authorName = computed(() => settings.value.author || '作者')
const authorAvatar = computed(() => settings.value.avatar || '')
</script>

<template>
  <article class="notion-card">
    <RouterLink :to="`/article/${article.id}`" class="notion-card__link">
      <div class="notion-card__cover">
        <img
          v-if="article.cover"
          :src="article.cover"
          :alt="article.title"
          loading="lazy"
        />
        <div v-else class="notion-card__cover-empty">暂无封面</div>
      </div>

      <div class="notion-card__body">
        <div class="notion-card__heading">
          <h2 class="notion-card__title">{{ article.title }}</h2>
          <span v-if="article.categoryName" class="notion-card__category">
            {{ article.categoryName }}
          </span>
        </div>
        <p class="notion-card__summary">
          {{ article.summary || '暂无摘要' }}
        </p>

        <div class="notion-card__author">
          <img
            v-if="authorAvatar"
            :src="authorAvatar"
            :alt="authorName"
            class="notion-card__avatar"
          />
          <span v-else class="notion-card__avatar notion-card__avatar--fallback">
            {{ authorName.slice(0, 1) }}
          </span>
          <div class="notion-card__author-meta">
            <span class="notion-card__author-name">{{ authorName }}</span>
          </div>
        </div>
      </div>
    </RouterLink>
  </article>
</template>

<style scoped>
.notion-card {
  min-width: 0;
}

.notion-card__link {
  display: flex;
  flex-direction: column;
  height: 100%;
  color: inherit;
  text-decoration: none;
}

.notion-card__cover {
  aspect-ratio: 16 / 10;
  border-radius: 12px;
  overflow: hidden;
  background: #f3f1ec;
}

.notion-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.notion-card__link:hover .notion-card__cover img {
  transform: scale(1.03);
}

.notion-card__cover-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9a9a9a;
  font-size: 0.875rem;
  background: linear-gradient(145deg, #f6f3ee, #ebe7df);
}

.notion-card__body {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding-top: 1.1rem;
}

.notion-card__heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 0.75rem;
}

.notion-card__category {
  flex-shrink: 0;
  margin: 0;
  font-size: 0.8rem;
  color: #8a8a8a;
  letter-spacing: 0.01em;
  line-height: 1.35;
  white-space: nowrap;
}

.notion-card__title {
  margin: 0;
  min-width: 0;
  flex: 1;
  font-size: 1.25rem;
  font-weight: 700;
  line-height: 1.35;
  letter-spacing: -0.02em;
  color: #0f0f0f;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notion-card__link:hover .notion-card__title {
  text-decoration: underline;
  text-underline-offset: 3px;
}

.notion-card__summary {
  margin: 0.65rem 0 0;
  font-size: 0.925rem;
  line-height: 1.55;
  color: #6b6b6b;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.notion-card__author {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  margin-top: 1.15rem;
}

.notion-card__avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.notion-card__avatar--fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ebe7df;
  color: #5a5a5a;
  font-size: 0.8rem;
  font-weight: 600;
}

.notion-card__author-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.25;
}

.notion-card__author-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f0f0f;
}
</style>
