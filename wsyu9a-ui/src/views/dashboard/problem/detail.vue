<template>
  <div class="problem-detail">
    <!-- 返回按钮 -->
    <el-button 
      type="primary" 
      plain
      size="default"
      @click="handleBack"
      class="back-btn"
    >
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>

    <!-- 标题栏 -->
    <div class="detail-header">
      <div class="title-section">
        <div class="header-top">
          <h2 class="title">{{ problem.title }}</h2>
        </div>
        <div class="meta">
          <div class="meta-item">
            <el-tag
              :type="getDifficultyType(problem.difficulty)"
              size="large"
              effect="dark"
              class="difficulty-tag"
            >
              {{ problem.difficulty === 'EASY' ? '简单' : 
                 problem.difficulty === 'MEDIUM' ? '中等' : '困难' }}
            </el-tag>
          </div>
          <div class="meta-item">
            <el-icon><Collection /></el-icon>
            <span>分类：{{ problem.categoryName }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Trophy /></el-icon>
            <span>积分：{{ problem.score }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Timer /></el-icon>
            <span>发布时间：{{ formatDate(problem.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="detail-content">
      <div v-loading="loading" class="content-wrapper">
        <!-- 左侧：题目描述 -->
        <div class="markdown-container">
          <div v-if="readme" v-html="markdownHtml" class="markdown-body"></div>
          <el-empty v-else description="暂无题目描述" />
        </div>

        <!-- 右侧：解题功能区 -->
        <div class="solve-sidebar">
          <!-- 环境控制卡片 -->
          <el-card class="env-card">
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

          <!-- Flag提交卡片 -->
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

          <!-- 附件下载卡片 -->
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

          <!-- 题目信息卡片 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <h3>题目信息</h3>
              </div>
            </template>
            <div class="info-list">
              <div class="info-item">
                <span class="label">提交次数</span>
                <span class="value">{{ problem.submitCount || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="label">通过率</span>
                <span class="value">{{ problem.passRate || '0%' }}</span>
              </div>
              <div class="info-item">
                <span class="label">解决人数</span>
                <span class="value">{{ problem.solvedCount || 0 }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { 
  Collection, 
  Trophy, 
  Timer,
  InfoFilled,
  CaretRight,
  ArrowLeft,
  VideoPlay,
  VideoPause,
  Document,
  Download,
  CopyDocument
} from '@element-plus/icons-vue'
import { getUserProblemDetail, getProblemReadme } from '@/api/problem'
import { marked } from 'marked'
import 'github-markdown-css'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const problem = ref({})
const readme = ref('')

// 返回上一页
const handleBack = () => {
  router.go(-1)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 将 Markdown 转换为 HTML
const markdownHtml = computed(() => {
  return readme.value ? marked(readme.value) : ''
})

// 获取 README 内容
const getReadmeContent = async (path) => {
  try {
    const res = await getProblemReadme(path)
    if (res.code === 200) {
      readme.value = res.data
    } else {
      throw new Error(res.message)
    }
  } catch (error) {
    console.error('获取README内容失败:', error)
    ElMessage.error('获取题目描述失败')
  }
}

// 获取题目详情
const getDetail = async () => {
  loading.value = true
  try {
    const res = await getUserProblemDetail(route.params.id)
    if (res.code === 200) {
      problem.value = res.data
      // 如果有 detail 字段，则获取 README 内容
      if (res.data.detail) {
        await getReadmeContent(res.data.detail)
      }
    }
  } catch (error) {
    console.error('获取题目详情失败:', error)
    ElMessage.error('获取题目详情失败')
  } finally {
    loading.value = false
  }
}

// 获取难度标签类型
const getDifficultyType = (difficulty) => {
  const types = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return types[difficulty] || 'info'
}

// 添加解题相关的数据
const envStatus = ref('stopped')
const isStarting = ref(false)
const isSubmitting = ref(false)
const envUrl = ref('')
const remainingTime = ref('29:59')
const submitForm = ref({ flag: '' })
const attachments = ref([
  { name: 'writeup.pdf', url: '#' },
  { name: 'source.zip', url: '#' }
])

// 添加解题相关的方法
const handleStartEnv = async () => {
  isStarting.value = true
  envStatus.value = 'starting'
  
  try {
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

const handleStopEnv = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    envStatus.value = 'stopped'
    envUrl.value = ''
    ElMessage.success('环境已停止')
  } catch (error) {
    ElMessage.error('停止环境失败')
  }
}

const copyUrl = () => {
  navigator.clipboard.writeText(envUrl.value)
  ElMessage.success('已复制到剪贴板')
}

const handleSubmit = async () => {
  if (!submitForm.value.flag) {
    ElMessage.warning('请输入flag')
    return
  }

  isSubmitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('提交成功')
    submitForm.value.flag = ''
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    isSubmitting.value = false
  }
}

const downloadFile = (file) => {
  ElMessage.success(`开始下载：${file.name}`)
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
.problem-detail {
  padding: 16px;
  min-height: 100vh;
  background-color: #f5f7fa;
  max-width: 1440px;
  margin: 0 auto;
  position: relative;  // 为绝对定位的返回按钮提供参考

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

  .detail-header {
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    margin: 48px auto 16px;  // 增加顶部边距，为返回按钮留出空间
    max-width: 1200px;

    .header-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .title {
        font-size: 24px;
        color: #303133;
        margin: 0;
        font-weight: 600;
      }
    }

    .meta {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;

      .meta-item {
        display: flex;
        align-items: center;
        color: #606266;
        font-size: 14px;

        .el-icon {
          margin-right: 8px;
          font-size: 16px;
        }

        .difficulty-tag {
          font-size: 14px;
          padding: 4px 10px;
        }
      }
    }
  }

  .detail-content {
    .content-wrapper {
      display: flex;
      gap: 16px;
      max-width: 1200px;
      margin: 0 auto;
      position: relative;

      .markdown-container {
        flex: 1;
        background: #fff;
        padding: 24px;
        border-radius: 12px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        max-width: 900px;

        :deep(.markdown-body) {
          background: transparent;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
          font-size: 15px;
          line-height: 1.6;
          color: #2c3e50;
          max-width: 100%;
          overflow-x: auto;

          h1, h2, h3, h4, h5, h6 {
            margin-top: 20px;
            margin-bottom: 12px;
            font-weight: 600;
            line-height: 1.25;
          }

          h1 {
            font-size: 1.8em;
            padding-bottom: 0.3em;
            border-bottom: 1px solid #eaecef;
          }

          h2 {
            font-size: 1.4em;
            padding-bottom: 0.3em;
            border-bottom: 1px solid #eaecef;
          }

          p {
            margin-top: 0;
            margin-bottom: 12px;
          }

          code {
            padding: 0.2em 0.4em;
            margin: 0;
            font-size: 85%;
            background-color: rgba(27,31,35,0.05);
            border-radius: 3px;
          }

          pre {
            padding: 16px;
            overflow: auto;
            font-size: 85%;
            line-height: 1.45;
            background-color: #f6f8fa;
            border-radius: 3px;

            code {
              padding: 0;
              margin: 0;
              background-color: transparent;
              border: 0;
            }
          }

          img {
            max-width: 100%;
            box-sizing: border-box;
          }

          blockquote {
            padding: 0 1em;
            color: #6a737d;
            border-left: 0.25em solid #dfe2e5;
            margin: 0 0 16px 0;
          }

          ul, ol {
            padding-left: 2em;
            margin-top: 0;
            margin-bottom: 16px;
          }

          table {
            display: block;
            width: 100%;
            overflow: auto;
            margin-top: 0;
            margin-bottom: 16px;
            border-spacing: 0;
            border-collapse: collapse;

            th, td {
              padding: 6px 13px;
              border: 1px solid #dfe2e5;
            }

            tr {
              background-color: #fff;
              border-top: 1px solid #c6cbd1;

              &:nth-child(2n) {
                background-color: #f6f8fa;
              }
            }
          }
        }
      }

      .solve-sidebar {
        width: 320px;
        position: sticky;
        top: 16px;
        align-self: flex-start;
        max-height: calc(100vh - 32px);
        overflow-y: auto;

        .env-card,
        .submit-card,
        .attachments-card,
        .info-card {
          margin-bottom: 16px;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

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

        .env-controls {
          .control-group {
            margin-bottom: 16px;
            display: flex;
            gap: 12px;
          }

          .env-info {
            .info-item {
              display: flex;
              align-items: center;
              margin-bottom: 12px;

              .label {
                width: 80px;
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
}

// 响应式布局
@media screen and (max-width: 1200px) {
  .problem-detail {
    padding: 12px;

    .detail-content {
      .content-wrapper {
        flex-direction: row;
        flex-wrap: wrap;
        justify-content: center;
        gap: 12px;

        .markdown-container {
          min-width: 0;
          width: calc(100% - 308px);  // 280px + 28px(gap)
        }

        .solve-sidebar {
          width: 100%;
          position: static;
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
          gap: 16px;
          margin-top: 16px;

          .env-card,
          .submit-card,
          .attachments-card,
          .info-card {
            margin-bottom: 0;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 900px) {
  .problem-detail {
    .detail-content {
      .content-wrapper {
        .markdown-container {
          width: 100%;
        }

        .solve-sidebar {
          width: 100%;
          display: flex;
          gap: 12px;
          position: static;

          .env-card,
          .submit-card,
          .attachments-card,
          .info-card {
            flex: 1;
            margin-bottom: 0;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .problem-detail {
    padding: 12px;

    .back-btn {
      left: 12px;
      top: 12px;
      height: 28px;
      padding: 0 10px;
    }

    .detail-header {
      padding: 16px;
      margin-top: 40px;  // 调整移动端顶部边距

      .header-top {
        flex-direction: row;
        align-items: center;
        gap: 12px;

        .title {
          font-size: 20px;
          flex: 1;
          margin-right: 12px;
        }
      }

      .meta {
        gap: 12px;
        margin-top: 12px;
      }
    }

    .detail-content {
      .content-wrapper {
        gap: 12px;
        
        .markdown-container {
          padding: 16px;
        }

        .solve-sidebar {
          flex-direction: column;
          
          .env-card,
          .submit-card,
          .attachments-card,
          .info-card {
            width: 100%;
          }
        }
      }
    }
  }
}
</style> 