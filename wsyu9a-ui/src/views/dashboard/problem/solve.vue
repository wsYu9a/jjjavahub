<template>
  <div class="problem-solve">
    <!-- 返回按钮 -->
    <el-button 
      type="primary" 
      plain
      size="default"
      @click="router.go(-1)"
      class="back-btn"
    >
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>

    <!-- 主要内容区域 -->
    <div class="solve-container">
      <!-- 题目信息 -->
      <div class="problem-info">
        <h2 class="title">{{ problem.title }}</h2>
        <div class="meta">
          <el-tag :type="getDifficultyType(problem.difficulty)" effect="dark">
            {{ problem.difficulty === 'EASY' ? '简单' : 
               problem.difficulty === 'MEDIUM' ? '中等' : '困难' }}
          </el-tag>
          <span class="score">
            <el-icon><Trophy /></el-icon>
            积分：{{ problem.score }}
          </span>
        </div>
      </div>

      <!-- 操作区域 -->
      <div class="action-area">
        <el-row :gutter="20">
          <!-- 左侧：环境控制 -->
          <el-col :span="16">
            <el-card class="environment-card">
              <template #header>
                <div class="card-header">
                  <h3>环境控制</h3>
                  <div class="env-status">
                    状态：
                    <el-tag 
                      :type="envStatus === 'running' ? 'success' : 
                            envStatus === 'starting' ? 'warning' : 'info'"
                    >
                      {{ envStatus === 'running' ? '运行中' :
                         envStatus === 'starting' ? '启动中' : '未启动' }}
                    </el-tag>
                  </div>
                </div>
              </template>
              
              <div class="env-controls">
                <div class="control-group">
                  <el-button 
                    type="primary" 
                    :loading="isStarting"
                    :disabled="envStatus === 'running'"
                    @click="handleStartEnv"
                  >
                    <el-icon><VideoPlay /></el-icon>
                    启动环境
                  </el-button>
                  <el-button 
                    type="danger" 
                    :disabled="envStatus === 'stopped'"
                    @click="handleStopEnv"
                  >
                    <el-icon><VideoPause /></el-icon>
                    停止环境
                  </el-button>
                </div>

                <div v-if="envStatus === 'running'" class="env-info">
                  <div class="info-item">
                    <span class="label">访问地址：</span>
                    <div class="value">
                      <el-link 
                        type="primary" 
                        :href="envUrl" 
                        target="_blank"
                      >
                        {{ envUrl }}
                      </el-link>
                      <el-button 
                        link 
                        type="primary"
                        @click="copyUrl"
                      >
                        <el-icon><CopyDocument /></el-icon>
                      </el-button>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="label">剩余时间：</span>
                    <span class="value countdown">{{ remainingTime }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 右侧：Flag提交和附件下载 -->
          <el-col :span="8">
            <el-card class="submit-card">
              <template #header>
                <div class="card-header">
                  <h3>提交 Flag</h3>
                </div>
              </template>
              
              <el-form :model="submitForm" @submit.prevent="handleSubmit">
                <el-form-item>
                  <el-input
                    v-model="submitForm.flag"
                    placeholder="请输入 flag"
                    clearable
                  />
                </el-form-item>
                <el-form-item>
                  <el-button 
                    type="primary" 
                    native-type="submit"
                    :loading="isSubmitting"
                    class="submit-btn"
                  >
                    提交
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <el-card v-if="attachments.length > 0" class="attachments-card">
              <template #header>
                <div class="card-header">
                  <h3>附件下载</h3>
                </div>
              </template>
              
              <div class="attachment-list">
                <div 
                  v-for="file in attachments" 
                  :key="file.name"
                  class="attachment-item"
                >
                  <span class="file-name">
                    <el-icon><Document /></el-icon>
                    {{ file.name }}
                  </span>
                  <el-button 
                    link 
                    type="primary"
                    @click="downloadFile(file)"
                  >
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft,
  Trophy,
  VideoPlay,
  VideoPause,
  Document,
  Download,
  CopyDocument
} from '@element-plus/icons-vue'
import { getUserProblemDetail } from '@/api/problem'

const route = useRoute()
const router = useRouter()

// 题目信息
const problem = ref({
  id: null,
  title: '',
  difficulty: '',
  score: 0
})

const envStatus = ref('stopped') // stopped, starting, running
const isStarting = ref(false)
const isSubmitting = ref(false)
const envUrl = ref('')
const remainingTime = ref('29:59')
const submitForm = ref({
  flag: ''
})
const attachments = ref([
  { name: 'writeup.pdf', url: '#' },
  { name: 'source.zip', url: '#' }
])

// 获取难度标签类型
const getDifficultyType = (difficulty) => {
  const types = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return types[difficulty] || 'info'
}

// 启动环境
const handleStartEnv = async () => {
  isStarting.value = true
  envStatus.value = 'starting'
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    envStatus.value = 'running'
    envUrl.value = 'http://challenge-1.example.com:8080'
    ElMessage.success('环境启动成功')
  } catch (error) {
    ElMessage.error('环境启动失败')
    envStatus.value = 'stopped'
  } finally {
    isStarting.value = false
  }
}

// 停止环境
const handleStopEnv = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    envStatus.value = 'stopped'
    envUrl.value = ''
    ElMessage.success('环境已停止')
  } catch (error) {
    ElMessage.error('停止环境失败')
  }
}

// 复制URL
const copyUrl = () => {
  navigator.clipboard.writeText(envUrl.value)
  ElMessage.success('已复制到剪贴板')
}

// 提交flag
const handleSubmit = async () => {
  if (!submitForm.value.flag) {
    ElMessage.warning('请输入flag')
    return
  }

  isSubmitting.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('提交成功')
    submitForm.value.flag = ''
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    isSubmitting.value = false
  }
}

// 下载文件
const downloadFile = (file) => {
  ElMessage.success(`开始下载：${file.name}`)
}

// 获取题目详情
const getDetail = async () => {
  try {
    const res = await getUserProblemDetail(route.params.id)
    if (res.code === 200) {
      problem.value = res.data
    }
  } catch (error) {
    console.error('获取题目详情失败:', error)
    ElMessage.error('获取题目详情失败')
  }
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
.problem-solve {
  padding: 16px;
  min-height: 100vh;
  background-color: #f5f7fa;
  position: relative;

  .back-btn {
    position: absolute;
    left: 16px;
    top: 16px;
    z-index: 10;
    height: 32px;
    padding: 0 12px;
    border-radius: 16px;
    font-weight: normal;
    background-color: #fff;
    
    .el-icon {
      margin-right: 2px;
      font-size: 14px;
    }
  }

  .solve-container {
    max-width: 1200px;
    margin: 0 auto;
    padding-top: 48px;

    .problem-info {
      background: #fff;
      padding: 20px;
      border-radius: 12px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      margin-bottom: 20px;

      .title {
        margin: 0 0 16px;
        font-size: 24px;
        color: #303133;
      }

      .meta {
        display: flex;
        align-items: center;
        gap: 16px;

        .score {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #606266;
        }
      }
    }

    .action-area {
      .environment-card,
      .submit-card,
      .attachments-card {
        margin-bottom: 20px;

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;

          h3 {
            margin: 0;
            font-size: 16px;
            font-weight: 600;
          }
        }
      }

      .environment-card {
        .env-controls {
          .control-group {
            margin-bottom: 20px;
            display: flex;
            gap: 12px;
          }

          .env-info {
            .info-item {
              display: flex;
              align-items: center;
              margin-bottom: 12px;

              .label {
                width: 100px;
                color: #606266;
              }

              .value {
                flex: 1;
                display: flex;
                align-items: center;
                gap: 8px;

                &.countdown {
                  color: #f56c6c;
                  font-weight: 500;
                }
              }
            }
          }
        }
      }

      .submit-card {
        .submit-btn {
          width: 100%;
        }
      }

      .attachments-card {
        .attachment-list {
          .attachment-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #ebeef5;

            &:last-child {
              border-bottom: none;
            }

            .file-name {
              display: flex;
              align-items: center;
              gap: 8px;
              color: #606266;
            }
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .problem-solve {
    padding: 12px;

    .back-btn {
      left: 12px;
      top: 12px;
      height: 28px;
      padding: 0 10px;
    }

    .solve-container {
      padding-top: 40px;

      .problem-info {
        padding: 16px;

        .title {
          font-size: 20px;
        }
      }

      .action-area {
        :deep(.el-row) {
          margin: 0 !important;
        }

        :deep(.el-col) {
          padding: 0 !important;
        }

        .environment-card,
        .submit-card,
        .attachments-card {
          margin-bottom: 16px;
        }
      }
    }
  }
}
</style> 