<template>
  <div class="profile">
    <div class="profile-container">
      <!-- 个人信息卡片 -->
      <el-row :gutter="16">
        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <el-card class="user-card">
            <div class="user-info">
              <el-avatar :size="120" :src="userInfo.avatar" />
              <h2 class="username">{{ userInfo.username }}</h2>
              <div class="role">{{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}</div>
              <div class="join-time">你已经加入平台 {{ daysSinceJoin }} 天</div>
              <el-button type="primary" size="large" @click="handleEdit">
                编辑资料
              </el-button>
            </div>
          </el-card>
        </el-col>

        <!-- 解题统计卡片 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
          <el-card class="stats-card">
            <template #header>
              <div class="card-header">
                <h3>解题统计</h3>
              </div>
            </template>
            <el-row :gutter="20" class="stats-row">
              <el-col :xs="24" :sm="12">
                <div class="stat-item">
                  <div class="label">总分</div>
                  <div class="value">{{ userStats.totalScore }}</div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="stat-item">
                  <div class="label">排名</div>
                  <div class="value">#{{ userStats.ranking }}</div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="stat-item">
                  <div class="label">解题数</div>
                  <div class="value">{{ userStats.solvedCount }}</div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="stat-item">
                  <div class="label">提交数</div>
                  <div class="value">{{ userStats.submitCount }}</div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>

      <!-- 解题分析图表 -->
      <el-row :gutter="16" class="charts-row">
        <el-col :xs="24" :sm="24" :md="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <h3>解题难度分布</h3>
              </div>
            </template>
            <div class="chart-container">
              <div ref="difficultyChartRef" class="radar-chart"></div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <h3>分类解题统计</h3>
              </div>
            </template>
            <div class="chart-container">
              <div ref="categoryChartRef" class="radar-chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 最近解题记录 -->
      <el-card class="recent-solves">
        <template #header>
          <div class="card-header">
            <h3>最近解题</h3>
          </div>
        </template>
        <el-table :data="recentSolves" stripe>
          <el-table-column prop="problemTitle" label="题目" min-width="200" />
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="{ row }">
              <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                {{ row.difficulty === 'EASY' ? '简单' : 
                   row.difficulty === 'MEDIUM' ? '中等' : '困难' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100" />
          <el-table-column prop="solveTime" label="解题时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.solveTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="editForm.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">
            支持 jpg 格式，大小不超过 2MB
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSaveProfile">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import {
  Trophy,
  Medal,
  Check,
  Upload,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue'
import { getUserStats } from '@/api/user'

// 用户信息
const userInfo = ref({
  username: '',
  role: '',
  avatar: '',
  createTime: ''
})

// 用户统计
const userStats = ref({
  totalScore: 0,
  ranking: 0,
  solvedCount: 0,
  submitCount: 0
})

// 计算加入天数
const daysSinceJoin = computed(() => {
  if (!userInfo.value.createTime) return 0
  const joinDate = dayjs(userInfo.value.createTime)
  const today = dayjs()
  return Math.ceil(today.diff(joinDate, 'day', true))
})

// 难度分布统计
const difficultyStats = ref([
  { difficulty: 'EASY', name: '简单', solved: 0, total: 30, color: '#67C23A' },
  { difficulty: 'MEDIUM', name: '中等', solved: 0, total: 20, color: '#E6A23C' },
  { difficulty: 'HARD', name: '困难', solved: 0, total: 10, color: '#F56C6C' }
])

// 分类统计
const categoryStats = ref([])

// 最近解题记录
const recentSolves = ref([])

// 编辑表单
const showEditDialog = ref(false)
const editForm = ref({
  username: '',
  avatar: ''
})

const editFormRef = ref(null)

const editRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ]
}

// 雷达图相关
const difficultyChartRef = ref(null)
let difficultyChart = null
const categoryChartRef = ref(null)
let categoryChart = null

// 初始化难度分布图
const initDifficultyChart = () => {
  if (!difficultyChartRef.value) return

  difficultyChart = echarts.init(difficultyChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}/{d}题 ({e}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '16',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: difficultyStats.value.map(item => ({
        value: item.solved,
        name: item.name,
        itemStyle: {
          color: item.color
        }
      }))
    }]
  }
  difficultyChart.setOption(option)
}

// 初始化分类统计图
const initCategoryChart = () => {
  if (!categoryChartRef.value) return

  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}题'
    },
    radar: {
      indicator: categoryStats.value.map(item => ({
        name: item.category,
        max: Math.max(...categoryStats.value.map(i => i.solved)) + 1 // 动态设置最大值
      }))
    },
    series: [{
      type: 'radar',
      data: [{
        value: categoryStats.value.map(item => item.solved),
        name: '已解题数',
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.2)'
        },
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }]
    }]
  }
  categoryChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  difficultyChart?.resize()
  categoryChart?.resize()
}

// 工具方法
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

// 头像上传
const handleAvatarSuccess = (res) => {
  editForm.value.avatar = res.url
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 保存个人信息
const handleSaveProfile = () => {
  editFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      // TODO: 调用API保存个人信息
      // const res = await updateUserProfile(editForm.value)
      ElMessage.success('保存成功')
      showEditDialog.value = false
      // 更新本地用户信息
      userInfo.value.username = editForm.value.username
      if (editForm.value.avatar) {
        userInfo.value.avatar = editForm.value.avatar
      }
    } catch (error) {
      ElMessage.error('保存失败')
    }
  })
}

// 打开编辑对话框时初始化表单
const handleEdit = () => {
  editForm.value.username = userInfo.value.username
  editForm.value.avatar = userInfo.value.avatar
  showEditDialog.value = true
}

const fetchUserStats = async () => {
  try {
    const response = await getUserStats()
    if (response.code === 200) {
      userStats.value = response.data
      userInfo.value = {
        username: response.data.username,
        role: response.data.role,
        avatar: response.data.avatar,
        createTime: response.data.joinTime
      }

      // 更新难度分布统计
      const stats = response.data.difficultyStats || {}
      difficultyStats.value = difficultyStats.value.map(item => ({
        ...item,
        solved: stats[item.difficulty] || 0
      }))

      // 更新分类统计
      categoryStats.value = Object.entries(response.data.categoryStats || {}).map(([category, solved]) => ({
        category,
        solved
      }))

      // 更新最近解题记录
      recentSolves.value = response.data.recentSolves || []

      // 初始化图表
      initDifficultyChart()
      initCategoryChart()
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

onMounted(() => {
  fetchUserStats()
  initDifficultyChart()
  initCategoryChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  difficultyChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style lang="scss" scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f7fa;
  
  .profile-container {
    max-width: 1440px;
    margin: 0 auto;
    padding: 16px;
  }

  .user-card {
    background-color: #fff;
    .user-info {
      text-align: center;
      padding: 24px 0;

      .username {
        margin: 16px 0 8px;
        font-size: 28px;
        color: #303133;
      }

      .role {
        color: #909399;
        margin-bottom: 8px;
        font-size: 16px;
      }

      .join-time {
        color: #606266;
        margin-bottom: 20px;
        font-size: 15px;
      }
    }
  }

  .stats-card {
    background: linear-gradient(135deg, #f6f8fc 0%, #ffffff 100%);
    border: none;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    height: 100%;
    display: flex;
    flex-direction: column;

    .stats-row {
      margin: 0 !important;
      flex: 1;
      display: flex;
      align-items: center;
      padding: 16px 0;
      
      .el-col {
        display: flex;
        align-items: stretch;
      }
    }

    :deep(.el-card__body) {
      padding: 0 16px;
      height: 100%;
      display: flex;
      flex-direction: column;
    }

    .stat-item {
      text-align: center;
      padding: 24px 16px;
      background: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 100%;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      .label {
        color: #909399;
        margin: 0 0 8px;
        font-size: 16px;
      }

      .value {
        font-size: 32px;
        font-weight: bold;
        color: #303133;
      }
    }

    :deep(.el-card__header) {
      padding: 16px 20px;
      border-bottom: 1px solid #ebeef5;
    }

    .card-header {
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    @media screen and (max-width: 768px) {
      .stats-row {
        padding: 12px 0;
      }

      .stat-item {
        padding: 20px 12px;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .value {
          font-size: 28px;
        }

        .label {
          margin: 0 0 6px;
        }
      }
    }
  }

  .charts-row {
    margin-top: 16px;
  }

  .chart-card {
    .chart-container {
      padding: 16px;

      .radar-chart {
        height: 300px;
        width: 100%;
      }
    }
  }

  .recent-solves {
    margin-top: 16px;
  }

  .card-header {
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
    }
  }

  @media screen and (max-width: 768px) {
    .profile-container {
      padding: 12px;
    }

    .el-row {
      margin: 0 !important;
    }

    .el-col {
      padding: 0 !important;
    }

    .user-card,
    .stats-card,
    .chart-card,
    .recent-solves {
      margin-bottom: 12px;
    }

    .charts-row {
      margin-top: 0;
    }

    .user-card {
      .user-info {
        padding: 20px 0;
        
        .username {
          font-size: 24px;
        }
        
        .role {
          font-size: 14px;
        }
        
        .join-time {
          font-size: 14px;
          margin-bottom: 16px;
        }
      }
    }

    .stats-card {
      :deep(.el-card__header) {
        padding: 12px 16px;
      }

      .stat-item {
        padding: 12px;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .value {
          font-size: 24px;
        }
      }
    }
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
    text-align: center;
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style> 