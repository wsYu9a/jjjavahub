<template>
  <div class="user-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名/邮箱"
            style="width: 200px"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="filteredUserList"
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
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="email" label="邮箱" width="250" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.role !== 'ADMIN'"
              :type="row.enabled ? 'danger' : 'success'"
              size="small"
              @click="handleStatusChange(row)"
            >
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, setUserStatus } from '@/api/admin'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const searchQuery = ref('')

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  searchKey: ''
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams.value)
    if (res.code === 200) {
      userList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  fetchUserList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  fetchUserList()
}

// 处理搜索
const handleSearch = () => {
  queryParams.value.pageNum = 1  // 重置到第一页
  queryParams.value.searchKey = searchQuery.value
  fetchUserList()
}

// 过滤用户列表
const filteredUserList = computed(() => {
  if (!userList.value) return []
  const query = searchQuery.value.toLowerCase()
  if (!query) return userList.value
  
  return userList.value.filter(user => 
    user.username.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  )
})

// 处理状态变更
const handleStatusChange = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.enabled ? '禁用' : '启用'}用户 "${user.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await setUserStatus({
      userId: user.id,
      enabled: !user.enabled
    })
    
    ElMessage.success(`${user.enabled ? '禁用' : '启用'}成功`)
    await fetchUserList()  // 刷新列表
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-manage {
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
}

:deep(.el-card__header) {
  padding: 10px 20px;
}

:deep(.el-card__body) {
  padding: 10px;
}

.el-tag {
  text-transform: capitalize;
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

:deep(.el-table .cell.username-cell),
:deep(.el-table .cell.email-cell) {
  justify-content: flex-start;
}

:deep(.el-table .cell.operation-cell) {
  justify-content: flex-end;
}

:deep(.el-table__row) {
  height: 45px;
}
</style> 