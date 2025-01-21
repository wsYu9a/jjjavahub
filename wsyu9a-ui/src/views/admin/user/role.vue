<template>
  <div class="role-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
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
        :data="userList"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa' }"
        :cell-style="{ padding: '4px 0' }"
      >
        <el-table-column label="ID" width="80">
          <template #default="{ $index }">
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="email" label="邮箱" width="250" />
        <el-table-column prop="role" label="角色" width="200">
          <template #default="{ row }">
            <el-select
              v-model="row.role"
              :disabled="row.username === 'admin'"
              @change="(value) => handleRoleChange(row, value)"
            >
              <el-option label="管理员" value="ADMIN" />
              <el-option label="普通用户" value="USER" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, setUserStatus, setUserRole } from '@/api/admin'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const searchQuery = ref('')

// 分页相关的响应式变量
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  searchKey: searchQuery.value
}))

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

// 监听分页变化
watch([currentPage, pageSize], () => {
  fetchUserList()
})

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1  // 搜索时重置到第一页
  fetchUserList()
}

// 处理角色变更
const handleRoleChange = async (user, newRole) => {
  if (user.username === 'admin') return
  
  try {
    await ElMessageBox.confirm(
      `确定要将用户 "${user.username}" 的角色修改为 ${newRole === 'ADMIN' ? '管理员' : '普通用户'} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await setUserRole({
      userId: user.id,
      role: newRole
    })
    
    if (res.code === 200) {
      ElMessage.success('角色修改成功')
      await fetchUserList()  // 刷新列表
    } else {
      ElMessage.error(res.message || '操作失败')
      await fetchUserList()  // 恢复原来的角色值
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新用户角色失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
    await fetchUserList()  // 恢复原来的角色值
  }
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
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

:deep(.el-select) {
  width: 160px;
}

:deep(.el-select .el-input__wrapper) {
  padding: 0 12px;
}

:deep(.el-table .cell.username-cell),
:deep(.el-table .cell.email-cell) {
  justify-content: flex-start;
}
</style> 