<template>
  <div class="role-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <div class="header-operations">
            <el-input
              v-model="searchQuery"
              placeholder="搜索角色名称/描述"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
              新增角色
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="filteredRoleList"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa' }"
        :cell-style="{ padding: '4px 0' }"
      >
        <el-table-column label="ID" width="80">
          <template #default="{ $index }">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="角色名称" width="180" />
        <el-table-column prop="code" label="角色编码" width="180" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="enabled" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                :type="row.enabled ? 'danger' : 'success'"
                size="small"
                @click="handleStatusChange(row)"
              >
                {{ row.enabled ? '禁用' : '启用' }}
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submitting"
          @click="submitForm"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getRoleList, addRole, updateRole, setRoleStatus } from '@/api/admin'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const submitting = ref(false)
const roleList = ref([])
const total = ref(0)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 分页相关的响应式变量
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  searchKey: searchQuery.value
}))

// 表单数据
const form = ref({
  id: null,
  name: '',
  code: '',
  description: '',
  enabled: true
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入角色描述', trigger: 'blur' }
  ]
}

// 获取角色列表
const fetchRoleList = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams.value)
    if (res.code === 200) {
      roleList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 过滤角色列表
const filteredRoleList = computed(() => {
  if (!roleList.value) return []
  const query = searchQuery.value.toLowerCase()
  if (!query) return roleList.value
  
  return roleList.value.filter(role => 
    role.name.toLowerCase().includes(query) ||
    role.description.toLowerCase().includes(query)
  )
})

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1  // 搜索时重置到第一页
  fetchRoleList()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: null,
    name: '',
    code: '',
    description: '',
    enabled: true
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (submitting.value) return
  
  try {
    submitting.value = true
    await formRef.value.validate()
    
    const api = dialogType.value === 'add' ? addRole : updateRole
    const res = await api(form.value)
    
    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      fetchRoleList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// 处理状态变更
const handleStatusChange = async (role) => {
  try {
    await ElMessageBox.confirm(
      `确定要${role.enabled ? '禁用' : '启用'}角色 "${role.name}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await setRoleStatus({
      roleId: role.id,
      enabled: !role.enabled
    })
    
    if (res.code === 200) {
      ElMessage.success(`${role.enabled ? '禁用' : '启用'}成功`)
      fetchRoleList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新角色状态失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 监听查询参数变化
watch(queryParams, () => {
  fetchRoleList()
}, { deep: true })

// 监听分页变化
watch([currentPage, pageSize], () => {
  fetchRoleList()
})

onMounted(() => {
  fetchRoleList()
})
</script>

<style lang="scss" scoped>
.role-manage {
  padding: 16px 24px;
}

.table-card {
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  
  .header-operations {
    display: flex;
    align-items: center;
  }
}

:deep(.el-card__header) {
  padding: 10px 20px;
}

:deep(.el-card__body) {
  padding: 10px;
}

.pagination-container {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table .cell) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-table__row) {
  height: 45px;
}
</style> 