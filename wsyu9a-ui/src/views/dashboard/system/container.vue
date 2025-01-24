<template>
  <div class="container-management">
    <div class="header">
      <h2>容器管理</h2>
      <el-button type="primary" @click="refreshContainers">刷新</el-button>
    </div>

    <el-table :data="containers" v-loading="loading" style="width: 100%; margin-top: 20px">
      <el-table-column prop="problemId" label="题目ID" width="100" />
      <el-table-column prop="problemTitle" label="题目名称" width="200" />
      <el-table-column prop="containerId" label="容器ID" width="220">
        <template #default="{ row }">
          <el-tooltip :content="row.containerId">
            <span>{{ row.containerId.substring(0, 12) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="port" label="端口" width="120" />
      <el-table-column prop="url" label="访问地址" min-width="200">
        <template #default="{ row }">
          <el-link type="primary" :href="row.url" target="_blank">{{ row.url }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="flag" label="Flag" width="220">
        <template #default="{ row }">
          <el-tooltip :content="row.flag">
            <span>{{ row.flag.substring(0, 20) }}...</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="expireTime" label="过期时间" width="180">
        <template #default="{ row }">
          <span :class="{ 'expire-soon': isExpireSoon(row.expireTime) }">
            {{ formatDateTime(row.expireTime) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleStop(row.problemId)"
            >
              停止
            </el-button>
            <el-button 
              type="primary" 
              size="small" 
              @click="handleExtend(row.problemId)"
            >
              延长时间
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 延长时间对话框 -->
    <el-dialog
      v-model="extendDialog.visible"
      title="延长运行时间"
      width="400px"
    >
      <el-form :model="extendDialog.form" label-width="100px">
        <el-form-item label="延长时间">
          <el-input-number 
            v-model="extendDialog.form.minutes" 
            :min="10" 
            :max="120"
            :step="10"
          />
          <span class="ml-2">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="extendDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="confirmExtend">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContainers, stopContainer, extendContainer } from '@/api/docker'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const containers = ref([])

const extendDialog = ref({
  visible: false,
  problemId: null,
  form: {
    minutes: 30
  }
})

// 获取所有容器
const refreshContainers = async () => {
  loading.value = true
  try {
    const res = await getContainers()
    containers.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取容器列表失败')
  } finally {
    loading.value = false
  }
}

// 停止容器
const handleStop = async (problemId) => {
  try {
    await ElMessageBox.confirm('确定要停止该容器吗？', '提示', {
      type: 'warning'
    })
    await stopContainer(problemId)
    ElMessage.success('停止成功')
    refreshContainers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '停止失败')
    }
  }
}

// 延长时间
const handleExtend = (problemId) => {
  extendDialog.value.problemId = problemId
  extendDialog.value.visible = true
}

// 确认延长时间
const confirmExtend = async () => {
  try {
    await extendContainer(
      extendDialog.value.problemId, 
      extendDialog.value.form.minutes
    )
    ElMessage.success('延长时间成功')
    extendDialog.value.visible = false
    refreshContainers()
  } catch (error) {
    ElMessage.error(error.message || '延长时间失败')
  }
}

// 判断是否即将过期（小于30分钟）
const isExpireSoon = (expireTime) => {
  const expire = new Date(expireTime)
  const now = new Date()
  return expire - now < 30 * 60 * 1000
}

onMounted(() => {
  refreshContainers()
})
</script>

<style scoped>
.container-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.expire-soon {
  color: #f56c6c;
}

.ml-2 {
  margin-left: 8px;
}
</style> 