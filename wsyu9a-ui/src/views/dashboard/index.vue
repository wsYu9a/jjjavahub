<template>
  <div class="dashboard-container">
    <!-- 侧边栏导航 -->
    <div class="sidebar">
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <div class="logo-container">
          <span class="logo-text">wsYu9a-jjjavahub</span>
        </div>
        
        <el-menu-item index="home">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="problems">
          <el-icon><Document /></el-icon>
          <span>题目</span>
        </el-menu-item>
        
        <el-menu-item index="leaderboard">
          <el-icon><Trophy /></el-icon>
          <span>排行榜</span>
        </el-menu-item>
        
        <el-menu-item index="profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        
        <el-menu-item index="announcements">
          <el-icon><Bell /></el-icon>
          <span>公告</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-bar">
        <div class="breadcrumb">
          <el-breadcrumb>
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ activeMenuLabel }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="user-info">
          <el-dropdown trigger="click">
            <div class="user-profile">
              
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/dashboard?activeMenu=profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/resetpwd')">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content">
        <!-- 首页内容 -->
        <div v-if="activeMenu === 'home'" class="module-container">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card class="welcome-card">
                <template #header>
                  <div class="card-header">
                    <h3>欢迎回来，{{ username }}</h3>
                  </div>
                </template>
                <div class="stats-container">
                  <div class="stat-item">
                    <h4>已解决题目</h4>
                    <div class="stat-value">{{ userStats.solvedCount }}</div>
                  </div>
                  <div class="stat-item">
                    <h4>积分</h4>
                    <div class="stat-value">{{ userStats.totalScore }}</div>
                  </div>
                  <div class="stat-item">
                    <h4>排名</h4>
                    <div class="stat-value">{{ userStats.ranking }}</div>
                  </div>
                </div>
              </el-card>
              <!-- 最新题目 -->
              <el-card class="latest-activities">
                <template #header>
                  <div class="card-header">
                    <h3>最新题目</h3>
                    <el-button text type="primary" @click="switchToProblems">
                      查看全部
                      <el-icon><ArrowRight /></el-icon>
                    </el-button>
                  </div>
                </template>
                
                <div class="activity-list">
                  <el-empty v-if="!loading && latestProblems.length === 0" description="暂无题目" />
                  <el-skeleton v-else-if="loading" :rows="5" animated />
                  <template v-else>
                    <div class="problem-cards">
                      <div v-for="item in latestProblems" 
                        :key="item.id" 
                        class="problem-card"
                        @click="handleProblemClick(item.id)"
                      >
                        <div class="card-header">
                          <el-tag 
                            :type="getDifficultyType(item.difficulty)" 
                            size="small" 
                            class="difficulty-tag"
                          >
                            {{ getDifficultyLabel(item.difficulty) }}
                          </el-tag>
                          <span class="score">{{ item.score }}分</span>
                        </div>
                        <div class="card-body">
                          <span class="title">
                            {{ item.title }}
                          </span>
                        </div>
                        <div class="card-footer">
                          <span class="category">{{ item.category }}</span>
                          <span class="time">
                            {{ isToday(item.createTime) ? formatTime(item.createTime) : formatDateTime(item.createTime) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </el-card>
            </el-col>
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
            <el-col :span="6">
              <el-card class="hot-problems">
                <template #header>
                  <div class="card-header">
                    <h3>热门题目</h3>
                  </div>
                </template>
                <div class="problem-list">
                  <div v-for="problem in hotProblems" :key="problem.id" class="problem-item">
                    <div class="problem-info">
                      <div class="title" @click="handleProblemClick(problem.id)">
                        {{ problem.title }}
                      </div>
                      <div class="stats">
                        <el-tag :type="getDifficultyType(problem.difficulty)" size="small">
                          {{ problem.difficulty === 'EASY' ? '简单' : 
                             problem.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                        </el-tag>
                        <span class="submission-count">
                          提交: {{ problem.submitCount || 0 }}次
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-card>
              <!-- 公告模块 -->
              <el-card class="announcement-card">
                <template #header>
                  <div class="card-header">
                    <h3>最新公告</h3>
                    <el-button 
                      text 
                      type="primary" 
                      @click="activeMenu = 'announcements'"
                    >
                      查看全部
                      <el-icon><ArrowRight /></el-icon>
                    </el-button>
                  </div>
                </template>
                <div class="announcement-list">
                  <el-skeleton v-if="announcementLoading" :rows="3" animated />
                  <template v-else>
                    <div 
                      v-for="item in latestAnnouncements" 
                      :key="item.id" 
                      class="announcement-item"
                    >
                      <div class="title-row">
                        <span class="title">{{ item.title }}</span>
                        <span class="time">
                          {{ isToday(item.createTime) ? formatTime(item.createTime) : formatDateTime(item.createTime) }}
                        </span>
                      </div>
                    </div>
                    <el-empty 
                      v-if="latestAnnouncements.length === 0" 
                      description="暂无公告" 
                    />
                  </template>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 公告模块 -->
        <div v-else-if="activeMenu === 'announcements'" class="module-container">
          <announcement-list />
        </div>

        <!-- 题目列表模块 -->
        <div v-else-if="activeMenu === 'problems'" class="module-container">
          <problem-list />
        </div>

        <!-- 排行榜模块 -->
        <div v-else-if="activeMenu === 'leaderboard'" class="module-container">
          <leader-board />
        </div>

        <!-- 个人信息模块 -->
        <div v-else-if="activeMenu === 'profile'" class="module-container">
          <user-profile />
        </div>

        <!-- 其他模块预留位置 -->
        <div v-else class="module-container">
          <el-empty :description="`${activeMenuLabel}模块开发中...`" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { House, Document, Trophy, User, Bell, Lock, SwitchButton, ArrowRight } from '@element-plus/icons-vue'
import AnnouncementList from './announcement/index.vue'
import ProblemList from './problem/index.vue'
import LeaderBoard from './leaderboard/index.vue'
import UserProfile from './profile/index.vue'
import dayjs from 'dayjs'
import { getUserProblemList, getHotProblems } from '@/api/problem'
import request from '@/utils/request'
import { getAnnouncementList } from '@/api/announcement'
import { formatDateTime, formatTime } from '@/utils/format'
import { getLatestSolveRecords } from '@/api/solve-records'
import { formatRelativeTime } from '@/utils/timeFormat'
import { getUserStats } from '@/api/user'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || '')
const activeMenu = ref('home')

const menuLabels = {
  home: '首页',
  problems: '题目',
  leaderboard: '排行榜',
  profile: '个人信息',
  announcements: '公告',
  'latest-activities': '最新活动'
}

const activeMenuLabel = ref(menuLabels[activeMenu.value])

const handleMenuSelect = (index) => {
  activeMenu.value = index
  activeMenuLabel.value = menuLabels[index]
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  
  ElMessage.success('退出成功')
  router.push('/login')
}

// 最新题目列表
const latestProblems = ref([])
const loading = ref(false)

// 获取最新题目列表
const fetchLatestProblems = async () => {
  const loadingInstance = ElLoading.service({
    target: '.latest-activities',
    text: '加载中...'
  })

  try {
    console.log('开始获取最新题目')
    loading.value = true

    const res = await getUserProblemList({
      pageNum: 1,
      pageSize: 10,
    })
    console.log('Received data:', res)
    if (res.code === 200) {
      latestProblems.value = (res.data.records || [])
        .sort((a, b) => new Date(b.updateTime) - new Date(a.updateTime))
        .slice(0, 5)
    } else {
      ElMessage.error(res.message || '获取题目失败')
    }
  } catch (error) {
    console.error('获取最新题目失败:', error)
    console.error('错误详情:', error.message)
    console.error('完整错误对象:', error)
    ElMessage.error('获取最新题目失败')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 工具方法
const formatDate = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

const getDifficultyType = (difficulty) => {
  const types = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return types[difficulty]
}

const getDifficultyLabel = (difficulty) => {
  const labels = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return labels[difficulty]
}

const handleProblemClick = (problemId) => {
  if (problemId) {
    router.push(`/problem/${problemId}`)
  }
}

// 切换到题目列表
const switchToProblems = () => {
  activeMenu.value = 'problems'
  activeMenuLabel.value = menuLabels['problems']
}

// 热门题目列表
const hotProblems = ref([])

// 获取热门题目
const fetchHotProblems = async () => {
  try {
    const res = await getHotProblems()
    if (res.code === 200) {
      hotProblems.value = res.data
    }
  } catch (error) {
    console.error('获取热门题目失败:', error)
  }
}

// 公告相关
const latestAnnouncements = ref([])
const announcementLoading = ref(false)

// 获取最新公告
const fetchLatestAnnouncements = async () => {
  try {
    announcementLoading.value = true
    const res = await getAnnouncementList({
      pageNum: 1,
      pageSize: 3  // 只获取最新的3条公告
    })
    if (res.code === 200) {
      latestAnnouncements.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  } finally {
    announcementLoading.value = false
  }
}

// 添加判断是否为当天的函数
const isToday = (dateStr) => {
  if (!dateStr) return false
  const date = new Date(dateStr)
  const today = new Date()
  return date.getDate() === today.getDate() &&
         date.getMonth() === today.getMonth() &&
         date.getFullYear() === today.getFullYear()
}

// 解题动态列表
const solveDynamics = ref([])

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

const userStats = ref({
  solvedCount: 0,
  score: 0,
  rank: '-'
})

const fetchUserStats = async () => {
  try {
    const res = await getUserStats()
    if (res.code === 200) {
      userStats.value = res.data
    }
  } catch (error) {
    console.error('获取用户统计信息失败:', error)
  }
}

// 修改监听路由参数变化
watch(
  () => route.query.activeMenu,
  (newActiveMenu) => {
    if (newActiveMenu) {
      activeMenu.value = newActiveMenu
      activeMenuLabel.value = menuLabels[newActiveMenu]
    }
  },
  { immediate: true }
)

onMounted(() => {
  console.log('组件挂载，准备获取最新题目')
  // 确保用户已登录
  const token = localStorage.getItem('token')
  if (!token) {
    console.error('用户未登录')
    return
  }
  fetchLatestProblems()
  fetchLatestAnnouncements()
  fetchLatestSolveRecords()
  fetchUserStats()
  fetchHotProblems()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  width: 240px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid #f0f0f0;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.sidebar-menu {
  border-right: none;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.top-bar {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.module-container {
  min-height: 500px;
}

.welcome-card {
  margin-bottom: 20px;
}

.stats-container {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-item h4 {
  margin: 0 0 10px 0;
  color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.recent-activity {
  height: 500px;
  display: flex;
  flex-direction: column;

  .hot-problems {
    flex: 1;
    padding: 0;
    overflow: hidden;

    .hot-item {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      border-bottom: 1px solid #ebeef5;
      transition: all 0.3s ease;

      &:first-child {
        border-top: none;
      }

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: #f5f7fa;
        transform: translateX(4px);
      }

      .rank-number {
        width: 30px;
        font-size: 18px;
        font-weight: bold;
        color: #909399;
      }

      .problem-info {
        flex: 1;

        .title {
          font-size: 15px;
          color: #303133;
          margin-bottom: 4px;
          cursor: pointer;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;

          &:hover {
            color: var(--el-color-primary);
          }
        }

        .stats {
          display: flex;
          align-items: center;
          gap: 8px;

          .completion-rate {
            font-size: 13px;
            color: #67c23a;
          }
        }
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

:deep(.el-menu-item) {
  display: flex;
  align-items: center;
}

:deep(.el-menu-item .el-icon) {
  margin-right: 10px;
}

.latest-activities {
  margin-top: 20px;
  position: relative;
  height: 580px;
  display: flex;
  flex-direction: column;

  .activity-list {
    flex: 1;
    overflow: hidden;
    padding: 16px;

    .problem-cards {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      height: calc(100% - 20px);

      .problem-card {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        height: calc((100% - 16px) / 2.5);
        min-height: 140px;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          .difficulty-tag {
            text-align: center;
            padding: 0 10px;
            height: 24px;
            line-height: 22px;
            font-size: 13px;
          }

          .score {
            color: #67c23a;
            font-size: 15px;
            font-weight: 500;
          }
        }

        .card-body {
          margin-bottom: 16px;
          flex: 1;
          display: flex;
          align-items: flex-start;

          .title {
            font-size: 16px;
            color: #303133;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            font-weight: 500;
            line-height: 1.5;
          }
        }

        .card-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          color: #909399;
          font-size: 14px;
          margin-top: 0;
        }
      }
    }
  }
}

.hot-problems {
  .problem-list {
    height: 360px;
    counter-reset: problem-index;
    
    .problem-item {
      padding: 20px 16px;
      border-bottom: 1px solid #ebeef5;
      transition: all 0.3s ease;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      height: 72px;
      display: flex;
      align-items: center;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: linear-gradient(to right, #f5f7fa, #ffffff);
        transform: translateX(4px);
      }

      &::before {
        content: counter(problem-index);
        counter-increment: problem-index;
        width: 28px;
        margin-right: 16px;
        font-size: 20px;
        font-weight: bold;
        color: #909399;
        text-align: center;
      }

      &:nth-child(1)::before {
        color: #f56c6c;
      }

      &:nth-child(2)::before {
        color: #e6a23c;
      }

      &:nth-child(3)::before {
        color: #67c23a;
      }

      .problem-info {
        flex: 1;
        min-width: 0;

        .title {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 8px;
          transition: color 0.3s ease;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;

          &:hover {
            color: #409EFF;
          }
        }

        .stats {
          display: flex;
          align-items: center;
          gap: 16px;

          .el-tag {
            border-radius: 12px;
            padding: 0 12px;
            height: 26px;
            line-height: 26px;
            font-size: 13px;
          }

          .submission-count {
            font-size: 14px;
            color: #67c23a;
            display: flex;
            align-items: center;
            gap: 6px;

            &::before {
              content: '📈';
              font-size: 16px;
            }
          }
        }
      }
    }
  }

  &.el-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    :deep(.el-card__header) {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      background: linear-gradient(to right, #f8f9fa, #fff);

      .card-header {
        h3 {
          position: relative;
          padding-left: 12px;
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 4px;
            height: 16px;
            background-color: #409EFF;
            border-radius: 2px;
          }
        }
      }
    }

    :deep(.el-card__body) {
      padding: 0;
    }
  }
}

.announcement-card {
  margin-top: 16px;
  height: calc(100% - 516px);
  display: flex;
  flex-direction: column;

  .card-header {
    background: linear-gradient(to right, #f8f9fa, #fff);
    h3 {
      position: relative;
      padding-left: 12px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: var(--el-color-primary);
        border-radius: 2px;
      }
    }
  }

  .announcement-list {
    flex: 1;
    overflow: hidden;

    .announcement-item {
      padding: 12px 16px;
      transition: all 0.3s ease;

      &:hover {
        background-color: #f5f7fa;
        transform: translateX(4px);
      }

      .title-row {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;
        
        .title {
          font-weight: 500;
          flex: 1;
        }
        
        .time {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

.solve-dynamics {
  height: 830px;
  display: flex;
  flex-direction: column;

  .card-header {
    flex-shrink: 0;
    background: linear-gradient(to right, #f8f9fa, #fff);
    h3 {
      position: relative;
      padding-left: 12px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: var(--el-color-primary);
        border-radius: 2px;
      }
    }
  }

  .dynamics-list {
    flex: 1;
    overflow: hidden;

    .dynamic-item {
      padding: 24px;
      border-bottom: 1px solid #ebeef5;
      transition: all 0.3s ease;
      height: calc((100% - 150px) / 5);
      display: flex;
      justify-content: space-between;
      align-items: center;

      &:hover {
        background-color: #f5f7fa;
        transform: translateX(4px);
        border-radius: 4px;
      }

      .dynamic-content {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
        
        .username {
          color: var(--el-color-primary);
          font-weight: 500;
          font-size: 16px;
        }

        .time {
          color: #909399;
          font-size: 14px;
        }

        .action {
          color: #606266;
          font-size: 14px;
        }
      }

      .problem-title {
        color: #303133;
        font-size: 16px;
        cursor: pointer;
        font-weight: 500;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        
        &:hover {
          color: var(--el-color-primary);
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }

  .latest-activities {
    .card-header {
      padding: 12px 16px;
    }

    .activity-list {
      .activity-item {
        padding: 12px 16px;

        .problem-info {
          .title-row {
            .title {
              font-size: 14px;
            }
          }

          .meta-info {
            font-size: 12px;
          }
        }
      }
    }
  }

  .hot-problems {
    .problem-list {
      height: 300px;

      .problem-item {
        padding: 16px;
        height: 60px;

        &::before {
          font-size: 18px;
          width: 24px;
          margin-right: 12px;
        }

        .problem-info {
          .title {
            font-size: 14px;
          }

          .stats {
            gap: 10px;

            .el-tag {
              height: 24px;
              line-height: 24px;
              font-size: 12px;
            }

            .submission-count {
              font-size: 12px;
            }
          }
        }
      }
    }
  }

  .announcement-card {
    margin-top: 12px;

    .announcement-list {
      .announcement-item {
        padding: 10px;

        .title {
          font-size: 13px;
        }

        .meta {
          font-size: 11px;
        }
      }
    }
  }

  .recent-activity {
    height: 450px;
  }

  .announcement-card {
    height: calc(100% - 466px);
  }
}

.activity-list {
  min-height: 200px;  /* 确保有足够的高度显示加载状态 */
}

:deep(.el-skeleton) {
  padding: 16px;
}

.problem-title {
  color: #303133;
  font-size: 16px;
  cursor: pointer;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  
  &:hover {
    color: var(--el-color-primary);
  }
}
</style> 