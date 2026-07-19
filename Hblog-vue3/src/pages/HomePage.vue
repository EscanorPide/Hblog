<script setup lang="ts">
import DefaultLayout from '@/layouts/DefaultLayout.vue'
</script>

<template>
  <DefaultLayout>
    <section class="home">
      <h1>Hblog</h1>
      <p>前端骨架已就绪，后续可在此接入文章列表。</p>
    </section>
  </DefaultLayout>
</template>

<style scoped>
.home h1 {
  margin: 0 0 0.5rem;
  font-size: 2rem;
}

.home p {
  margin: 0;
  color: #555;
}
</style>
