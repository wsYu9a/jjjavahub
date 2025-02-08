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
    <el-col :span="6">
              <el-card class="solve-dynamics">
                <template #header>
                  <div class="card-header">
                    <h3>解题动态</h3>
                  </div>
                </template>
                <div class="dynamics-list">
                  <template v-for="item in solveDynamics" :key="item.id">
                    <div class="dynamic-item">
                      <div class="dynamic-content">
                        <span class="username">{{ item.username }}</span>
                        <span class="time">{{ item.solveTime }}</span>
                        <span class="action">解决了</span>
                        <span 
                          class="problem-title" 
                          @click="handleProblemClick(item.problemId)"
                        >
                          {{ item.problemTitle }}
                        </span>
                      </div>
                    </div>
                  </template>
                </div>
              </el-card>
            </el-col>

    <el-form-item label="头像">
      <el-upload
        class="avatar-uploader"
        action="/api/user/upload/avatar"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        :headers="{ Authorization: `Bearer ${localStorage.getItem('token')}` }"
      >
        <img 
          v-if="editForm.avatar" 
          :src="editForm.avatar" 
          class="avatar" 
          alt="用户头像" 
        />
        <img 
          v-else 
          src="https://img2020.cnblogs.com/blog/1993669/202105/1993669-20210523191357495-836628456.png"
          class="avatar" 
          alt="默认头像" 
        />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <div class="upload-tip">
        支持 jpg 格式，大小不超过 2MB
      </div>
    </el-form-item>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/admin';
import { getLatestSolveRecords } from '@/api/solve-records'
import { User, Document, Folder, Bell, Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const stats = ref({
  userCount: 0,
  problemCount: 0,
  categoryCount: 0,
  announcementCount: 0
})

const solveDynamics = ref([])

const recentSolves = ref([])

const editForm = ref({
  avatar: ''
})

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

const fetchRecentSolves = async () => {
  try {
    const response = await getLatestSolveRecords()
    if (response.code === 200) {
      recentSolves.value = response.data
    } else {
      console.error('Failed to fetch recent solves')
    }
  } catch (error) {
    console.error('Error fetching recent solves:', error)
  }
}

// 获取最新解题记录
const fetchLatestSolveRecords = async () => {
  try {
    const res = await getLatestSolveRecords()
    if (res.code === 200) {
      solveDynamics.value = res.data.map(record => ({
        username: record.username,
        problemId: record.problemId,
        problemTitle: record.problemTitle,
        solveTime: formatRelativeTime(record.solveTime)
      }))
    }
  } catch (error) {
    console.error('获取解题动态失败:', error)
    ElMessage.error('获取解题动态失败')
  }
}

onMounted(() => {
  fetchDashboardStats()
  fetchRecentSolves()
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getDifficultyType = (difficulty) => {
  const types = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return types[difficulty] || 'info'
}

const formatProblemTitle = (title) => {
  // 假设题目标题格式为 "比赛名称 年份 题目名称"
  return title.replace(/(\d{4})/, '$1 ')
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

.recent-solves {
  margin-top: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}
</style> 