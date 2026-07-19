<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <header class="header">
    <RouterLink class="brand" to="/">Hblog</RouterLink>
    <nav class="nav">
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/about">关于</RouterLink>
    </nav>
  </header>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem 1.25rem;
}

.brand {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.nav {
  display: flex;
  gap: 1.25rem;
}

.nav a {
  opacity: 0.7;
}

.nav a.router-link-active {
  opacity: 1;
  font-weight: 600;
}
</style>
