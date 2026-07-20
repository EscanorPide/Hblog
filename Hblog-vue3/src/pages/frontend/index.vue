<template>
  <div class="min-h-screen flex flex-col">
    <!-- 顶部导航 -->
    <nav class="bg-white border-gray-200 border-b dark:bg-gray-900">
      <div class="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto p-4">
        <a href="/" class="flex items-center">
          <img src="https://flowbite.com/docs/images/logo.svg" class="h-8 mr-3" alt="Flowbite Logo" />
          <span class="self-center text-2xl font-semibold whitespace-nowrap dark:text-white"
            >博客</span
          >
        </a>
        <div class="flex items-center md:order-2">
          <button
            type="button"
            data-collapse-toggle="navbar-search"
            aria-controls="navbar-search"
            aria-expanded="false"
            class="md:hidden text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 focus:outline-none focus:ring-4 focus:ring-gray-200 dark:focus:ring-gray-700 rounded-lg text-sm p-2.5 mr-1"
          >
            <svg
              class="w-5 h-5"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 20 20"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
              />
            </svg>
            <span class="sr-only">Search</span>
          </button>
          <div class="relative hidden md:block">
            <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <svg
                class="w-4 h-4 text-gray-500 dark:text-gray-400"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 20 20"
              >
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                />
              </svg>
              <span class="sr-only">Search icon</span>
            </div>
            <input
              type="text"
              id="search-navbar"
              class="block w-full p-2 pl-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
              placeholder="请输入关键词..."
            />
          </div>
          <RouterLink
            to="/login"
            class="text-gray-900 ml-3 mr-1 hover:text-blue-700 cursor-pointer"
          >
            登录
          </RouterLink>
          <button
            data-collapse-toggle="navbar-search"
            type="button"
            class="inline-flex items-center p-2 w-10 h-10 justify-center text-sm text-gray-500 rounded-lg md:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:focus:ring-gray-600"
            aria-controls="navbar-search"
            aria-expanded="false"
          >
            <span class="sr-only">Open main menu</span>
            <svg
              class="w-5 h-5"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 17 14"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M1 1h15M1 7h15M1 13h15"
              />
            </svg>
          </button>
        </div>
        <div
          class="items-center justify-between hidden w-full md:flex md:w-auto md:order-1"
          id="navbar-search"
        >
          <div class="relative mt-3 md:hidden">
            <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <svg
                class="w-4 h-4 text-gray-500 dark:text-gray-400"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 20 20"
              >
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                />
              </svg>
            </div>
            <input
              type="text"
              id="search-navbar-mobile"
              class="block w-full p-2 pl-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
              placeholder="Search..."
            />
          </div>
          <ul
            class="flex flex-col p-4 md:p-0 mt-4 font-medium border border-gray-100 rounded-lg bg-gray-50 md:flex-row md:space-x-8 md:mt-0 md:border-0 md:bg-white dark:bg-gray-800 md:dark:bg-gray-900 dark:border-gray-700"
          >
            <li>
              <a
                href="#"
                class="block py-2 pl-3 pr-4 text-white bg-blue-700 rounded md:bg-transparent md:text-blue-700 md:p-0 md:dark:text-blue-500"
                aria-current="page"
                >首页</a
              >
            </li>
            <li>
              <a
                href="#"
                class="block py-2 pl-3 pr-4 text-gray-900 rounded hover:bg-gray-100 md:hover:bg-transparent md:hover:text-blue-700 md:p-0 md:dark:hover:text-blue-500 dark:text-white dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent dark:border-gray-700"
                >分类</a
              >
            </li>
            <li>
              <a
                href="#"
                class="block py-2 pl-3 pr-4 text-gray-900 rounded hover:bg-gray-100 md:hover:bg-transparent md:hover:text-blue-700 md:p-0 dark:text-white md:dark:hover:text-blue-500 dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent dark:border-gray-700"
                >标签</a
              >
            </li>
            <li>
              <a
                href="#"
                class="block py-2 pl-3 pr-4 text-gray-900 rounded hover:bg-gray-100 md:hover:bg-transparent md:hover:text-blue-700 md:p-0 dark:text-white md:dark:hover:text-blue-500 dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent dark:border-gray-700"
                >归档</a
              >
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- 主体内容 -->
    <main class="flex-1 w-full max-w-screen-xl mx-auto px-4 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 文章列表 -->
        <section class="lg:col-span-2 space-y-4">
          <article
            v-for="article in articles"
            :key="article.id"
            class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow"
          >
            <h2 class="text-xl font-semibold text-gray-900 mb-2 hover:text-blue-700 cursor-pointer">
              {{ article.title }}
            </h2>
            <p class="text-gray-600 text-sm mb-3 line-clamp-2">{{ article.summary }}</p>
            <div class="flex items-center gap-4 text-xs text-gray-400">
              <span>{{ article.category }}</span>
              <span>{{ article.date }}</span>
            </div>
          </article>
        </section>

        <!-- 侧边栏 -->
        <aside class="space-y-4">
          <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-3">关于我</h3>
            <p class="text-sm text-gray-600 leading-relaxed">
              一只会写代码的犬小哈，分享 Java / Vue 学习笔记。
            </p>
          </div>
          <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-3">热门分类</h3>
            <ul class="space-y-2 text-sm">
              <li v-for="item in categories" :key="item" class="text-gray-600 hover:text-blue-700 cursor-pointer">
                {{ item }}
              </li>
            </ul>
          </div>
        </aside>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="border-t border-gray-200 bg-white">
      <div class="max-w-screen-xl mx-auto px-4 py-4 text-center text-sm text-gray-500">
        公众号：小哈学Java 网站：quanxiaoha.com
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { initCollapses } from 'flowbite'

const articles = ref([
  {
    id: 1,
    title: 'Vue3 + Vite 博客项目起步',
    summary: '从零搭建前端工程，配置路由、Tailwind 与 Flowbite，快速搭好博客骨架。',
    category: '前端',
    date: '2026-07-19',
  },
  {
    id: 2,
    title: 'Spring Boot 多模块项目实践',
    summary: '介绍 web / admin / common 多模块拆分思路，以及常见依赖与配置组织方式。',
    category: '后端',
    date: '2026-07-18',
  },
  {
    id: 3,
    title: 'Tailwind CSS 快速上手',
    summary: '用工具类写出响应式导航和卡片布局，少写自定义 CSS，提升开发效率。',
    category: '前端',
    date: '2026-07-17',
  },
])

const categories = ref(['前端', '后端', '数据库', '运维'])

onMounted(() => {
  initCollapses()
})
</script>
