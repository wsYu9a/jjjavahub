<template>
  <div class="dashboard-container">
    <div class="dashboard">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>用户总数</span>
              </div>
            </template>
            <div class="card-body">
              <h2>{{ stats.userCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>题目总数</span>
              </div>
            </template>
            <div class="card-body">
              <h2>{{ stats.problemCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>分类总数</span>
              </div>
            </template>
            <div class="card-body">
              <h2>{{ stats.categoryCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>公告总数</span>
              </div>
            </template>
            <div class="card-body">
              <h2>{{ stats.announcementCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const stats = ref({
  userCount: 0,
  problemCount: 0,
  categoryCount: 0,
  announcementCount: 0
})

// TODO: 实现获取统计数据的接口
const getStats = async () => {
  // const res = await getAdminStats()
  // stats.value = res.data
}

const cleanup = () => {
  // 清理组件资源
}

onMounted(() => {
  getStats()
})

onBeforeUnmount(() => {
  cleanup()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .el-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-body {
    text-align: center;
    h2 {
      margin: 10px 0;
      font-size: 24px;
      color: #409EFF;
    }
  }
}
</style> 