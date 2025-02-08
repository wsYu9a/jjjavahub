<template>
  <div class="admin-dashboard">
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <el-icon><User /></el-icon>
              <div class="label">用户总数</div>
              <div class="value">{{ stats.userCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <el-icon><Document /></el-icon>
              <div class="label">题目总数</div>
              <div class="value">{{ stats.problemCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <el-icon><Folder /></el-icon>
              <div class="label">分类总数</div>
              <div class="value">{{ stats.categoryCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <el-icon><Bell /></el-icon>
              <div class="label">公告总数</div>
              <div class="value">{{ stats.announcementCount }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 添加排行榜表格 -->
    <el-card class="rank-table">
      <template #header>
        <div class="card-header">
          <h3>解题统计</h3>
        </div>
      </template>
      <el-table :data="rankings" stripe>
        <el-table-column label="排名" width="100" align="center">
          <template #default="{ $index }">
            <div class="rank">
              <el-tag :type="getRankType($index + 1)" effect="dark">
                {{ $index + 1 }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="'data:image/jpeg;base64,' + (row.avatar || defaultAvatar)" />
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="分数" width="120" align="center">
          <template #default="{ row }">
            <span class="score">{{ row.score }}</span>
          </template>
        </el-table-column>

        <el-table-column label="解题统计" align="center">
          <template #default="{ row }">
            <div class="solve-stats">
              <el-tooltip content="总解题数">
                <el-tag type="info" effect="plain">
                  {{ row.solvedCount }}
                </el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :max-page="getMaxPage()"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats, getRankingList } from '@/api/admin';
import { User, Document, Folder, Bell } from '@element-plus/icons-vue'

const stats = ref({
  userCount: 0,
  problemCount: 0,
  categoryCount: 0,
  announcementCount: 0,
})

const rankings = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchDashboardStats = async () => {
  try {
    const response = await getDashboardStats()
    if (response.code === 200) {
      stats.value = response.data
    } else {
      console.error('Failed to fetch dashboard stats')
    }
  } catch (error) {
    console.error('Error fetching dashboard stats:', error)
  }
}

const fetchRankings = async () => {
  try {
    const response = await getRankingList()
    if (response.code === 200) {
      rankings.value = response.data
      total.value = rankings.value.length; // 更新总数
    } else {
      console.error('Failed to fetch rankings')
    }
  } catch (error) {
    console.error('Error fetching rankings:', error)
  }
}

onMounted(() => {
  fetchDashboardStats()
  fetchRankings()
})

const getRankType = (rank) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return 'info'
}

const getMaxPage = () => {
  return Math.ceil(total.value / pageSize.value);
}
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #f6f8fc 0%, #ffffff 100%);
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-item {
  text-align: center;
  padding: 20px;
  .el-icon {
    font-size: 40px;
    color: #409EFF;
    margin-bottom: 10px;
  }
  .label {
    font-size: 16px;
    color: #909399;
  }
  .value {
    font-size: 32px;
    font-weight: bold;
    color: #303133;
  }
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.rank-table {
  margin-top: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.rank-table .el-table {
  border: none;
}

.rank-table .el-table th {
  background-color: #f5f7fa;
  color: #606266;
}

.rank-table .el-table td {
  color: #303133;
}

.rank-table .el-tag {
  font-weight: bold;
}
</style>