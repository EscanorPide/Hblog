<template>
  <div class="page">
    <header class="page-header">
      <div>
        <h1>文章管理</h1>
        <p>测试页面：用于验证菜单跳转与标签导航栏路由同步。</p>
      </div>
      <el-button type="primary">新建文章</el-button>
    </header>

    <section class="page-card">
      <el-table :data="rows" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
      </el-table>
    </section>
  </div>
</template>

<script setup>
defineOptions({ name: 'AdminArticles' })

const rows = [
  {
    id: 1,
    title: 'Vue 3 组件设计实践',
    category: '前端开发',
    status: '已发布',
    updatedAt: '2026-07-18 10:20',
  },
  {
    id: 2,
    title: 'Element Plus 后台布局笔记',
    category: '前端开发',
    status: '草稿',
    updatedAt: '2026-07-17 16:05',
  },
  {
    id: 3,
    title: '博客标签导航实现思路',
    category: '工程实践',
    status: '已发布',
    updatedAt: '2026-07-16 09:40',
  },
]
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.page-header h1 {
  margin: 0;
  color: #272a36;
  font-size: 22px;
  line-height: 1.3;
}

.page-header p {
  margin: 6px 0 0;
  color: #8b91a1;
  font-size: 13px;
}

.page-card {
  padding: 8px;
  border: 1px solid #eaedf2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 6px 22px rgba(30, 41, 59, 0.035);
}
</style>
