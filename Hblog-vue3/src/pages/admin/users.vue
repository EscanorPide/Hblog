<template>
  <div class="page">
    <section class="page-card filter-card">
      <el-form :model="filter" inline class="filter-form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="filter.username" placeholder="输入用户名" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="filter.role" placeholder="全部角色" clearable style="width: 160px">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" :loading="loading" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" :disabled="loading" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="page-card table-card">
      <div class="table-toolbar">
        <div>
          <h2>用户列表</h2>
          <p>管理后台账号及其访问角色</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="createVisible = true">新增用户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="用户名" min-width="180">
          <template #default="{ row }"><strong>{{ row.username }}</strong><el-tag v-if="row.username === currentUsername" class="self-tag" size="small">当前账号</el-tag></template>
        </el-table-column>
        <el-table-column label="角色" min-width="220">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" :type="roleType(role)" class="role-tag">{{ roleLabel(role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column prop="updateTime" label="更新时间" min-width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openRoles(row)">角色</el-button>
            <el-button type="warning" link :icon="Key" @click="openPassword(row)">重置密码</el-button>
            <el-button type="danger" link :icon="Delete" :disabled="row.username === currentUsername" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" background
          @size-change="fetchList" @current-change="fetchList" />
      </div>
    </section>

    <FormDialog ref="createDialog" v-model="createVisible" title="新增用户" width="480px" :form-model="createForm" :rules="createRules" :loading="submitLoading" @confirm="handleCreate" @closed="resetCreate">
      <el-form-item label="用户名" prop="username"><el-input v-model="createForm.username" maxlength="60" clearable autocomplete="off" /></el-form-item>
      <el-form-item label="初始密码" prop="password"><el-input v-model="createForm.password" type="password" show-password maxlength="72" autocomplete="new-password" /></el-form-item>
      <el-form-item label="角色" prop="roles"><el-select v-model="createForm.roles" multiple style="width: 100%"><el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
    </FormDialog>

    <FormDialog ref="rolesDialog" v-model="rolesVisible" title="调整角色" :form-model="rolesForm" :rules="rolesRules" :loading="submitLoading" @confirm="handleRoles" @closed="rolesDialog?.clearValidate()">
      <el-alert :title="`正在调整：${rolesForm.username}`" type="info" :closable="false" class="dialog-alert" />
      <el-form-item label="角色" prop="roles"><el-select v-model="rolesForm.roles" multiple style="width: 100%"><el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
    </FormDialog>

    <FormDialog ref="passwordDialog" v-model="passwordVisible" title="重置密码" :form-model="passwordForm" :rules="passwordRules" :loading="submitLoading" @confirm="handlePassword" @closed="resetPassword">
      <el-alert :title="`为 ${passwordForm.username} 设置新密码`" type="warning" :closable="false" class="dialog-alert" />
      <el-form-item label="新密码" prop="password"><el-input v-model="passwordForm.password" type="password" show-password maxlength="72" autocomplete="new-password" /></el-form-item>
    </FormDialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Key, Plus, RefreshRight, Search } from '@element-plus/icons-vue'
import FormDialog from '@/components/FormDialog.vue'
import { createUser, deleteUser, getUserPageList, resetUserPassword, updateUserRoles } from '@/api/admin/user'
import { showMessage, showModel } from '@/composables/util'
import { useUserStore } from '@/stores/user'

defineOptions({ name: 'AdminUsers' })
const roleOptions = [{ label: '管理员', value: 'admin' }, { label: '编辑', value: 'editor' }, { label: '普通用户', value: 'user' }]
const userStore = useUserStore()
const currentUsername = computed(() => userStore.userInfo?.username || '')
const loading = ref(false), submitLoading = ref(false), tableData = ref([])
const createVisible = ref(false), rolesVisible = ref(false), passwordVisible = ref(false)
const createDialog = ref(), rolesDialog = ref(), passwordDialog = ref()
const filter = reactive({ username: '', role: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const createForm = reactive({ username: '', password: '', roles: ['user'] })
const rolesForm = reactive({ id: null, username: '', roles: [] })
const passwordForm = reactive({ id: null, username: '', password: '' })
const roleRequired = [{ type: 'array', required: true, min: 1, message: '至少选择一个角色', trigger: 'change' }]
const createRules = { username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { max: 60, message: '不能超过 60 个字符', trigger: 'blur' }], password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 8, max: 72, message: '密码长度为 8-72 个字符', trigger: 'blur' }], roles: roleRequired }
const rolesRules = { roles: roleRequired }
const passwordRules = { password: createRules.password }

function roleLabel(role) { return roleOptions.find((item) => item.value === role)?.label || role }
function roleType(role) { return role === 'admin' ? 'danger' : role === 'editor' ? 'warning' : 'info' }
function fetchList() {
  if (loading.value) return
  loading.value = true
  getUserPageList({ current: pagination.current, size: pagination.size, username: filter.username.trim() || undefined, role: filter.role || undefined })
    .then((res) => { if (!res.success) return showMessage(res.message || '获取用户失败', 'error'); tableData.value = res.data || []; pagination.total = Number(res.total) || 0 })
    .finally(() => { loading.value = false })
}
function handleSearch() { pagination.current = 1; fetchList() }
function handleReset() { filter.username = ''; filter.role = ''; pagination.current = 1; fetchList() }
function request(action, success, close) { if (submitLoading.value) return; submitLoading.value = true; action().then((res) => { if (!res.success) return showMessage(res.message || '操作失败', 'error'); showMessage(success); close(); fetchList() }).finally(() => { submitLoading.value = false }) }
function handleCreate() { request(() => createUser({ username: createForm.username.trim(), password: createForm.password, roles: createForm.roles }), '用户创建成功', () => { createVisible.value = false; pagination.current = 1 }) }
function resetCreate() { Object.assign(createForm, { username: '', password: '', roles: ['user'] }); createDialog.value?.clearValidate() }
function openRoles(row) { Object.assign(rolesForm, { id: row.id, username: row.username, roles: [...(row.roles || [])] }); rolesVisible.value = true }
function handleRoles() { request(() => updateUserRoles({ id: rolesForm.id, roles: rolesForm.roles }), '角色更新成功', () => { rolesVisible.value = false; if (rolesForm.username === currentUsername.value) userStore.setUserInfo() }) }
function openPassword(row) { Object.assign(passwordForm, { id: row.id, username: row.username, password: '' }); passwordVisible.value = true }
function handlePassword() { request(() => resetUserPassword({ id: passwordForm.id, password: passwordForm.password }), '密码重置成功', () => { passwordVisible.value = false }) }
function resetPassword() { passwordForm.password = ''; passwordDialog.value?.clearValidate() }
function handleDelete(row) { showModel(`确认删除用户「${row.username}」吗？此操作不可恢复。`, 'warning', '删除用户').then(() => deleteUser({ id: row.id })).then((res) => { if (!res.success) return showMessage(res.message || '删除失败', 'error'); showMessage('用户已删除'); if (tableData.value.length === 1 && pagination.current > 1) pagination.current--; fetchList() }).catch(() => {}) }
onMounted(fetchList)
</script>

<style scoped>
.page{display:flex;flex-direction:column;gap:16px}.page-card{border:1px solid #eaedf2;border-radius:14px;background:#fff;box-shadow:0 6px 22px rgba(30,41,59,.035)}.filter-card{padding:18px 18px 4px}.filter-form :deep(.el-form-item){margin-right:18px;margin-bottom:14px}.table-card{padding:16px}.table-toolbar{display:flex;align-items:center;justify-content:space-between;margin-bottom:14px}.table-toolbar h2{margin:0;color:#222636;font-size:17px}.table-toolbar p{margin:4px 0 0;color:#8b91a1;font-size:12px}.pagination-wrap{display:flex;justify-content:flex-end;margin-top:16px}.role-tag,.self-tag{margin-left:7px}.dialog-alert{margin-bottom:18px}@media(max-width:640px){.table-toolbar{align-items:flex-start;gap:12px}.pagination-wrap{overflow-x:auto;justify-content:flex-start}}
</style>
