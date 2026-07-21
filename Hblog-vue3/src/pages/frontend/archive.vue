<script setup>
import CraftAside from './components/CraftAside.vue'
</script>

<template>
  <div class="craft-layout">
    <CraftAside />
    <section class="craft-main">
      <h2 class="craft-section-title">归档</h2>
      <p class="craft-empty">按时间归档即将开放，敬请期待。</p>
    </section>
  </div>
</template>

<style scoped>
.craft-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 2.5rem;
  align-items: start;
}

.craft-main {
  min-width: 0;
}

.craft-section-title {
  margin: 0 0 1rem;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #0f0f0f;
}

.craft-empty {
  color: #8a8a8a;
  font-size: 0.95rem;
}

@media (min-width: 1024px) {
  .craft-layout {
    grid-template-columns: 280px minmax(0, 1fr);
    gap: 4rem;
  }
}

@media (min-width: 1200px) {
  .craft-layout {
    grid-template-columns: 300px minmax(0, 1fr);
    gap: 5rem;
  }
}
</style>
