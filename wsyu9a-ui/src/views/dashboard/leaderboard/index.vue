<template>
  <div class="leaderboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">排行榜</h2>
      <el-button type="primary" @click="refreshRankings">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 排行榜内容 -->
    <div v-loading="loading" class="leaderboard-content">
      <!-- 前三名展示 -->
      <div class="top-three">
        <!-- 第二名 -->
        <div v-if="rankings[1]" class="rank-card second">
          <div class="medal">
            <el-icon><Medal /></el-icon>
            <span class="rank-num">2</span>
          </div>
          <el-avatar :size="80" :src="rankings[1].avatar" />
          <h3 class="username">{{ rankings[1].username }}</h3>
          <div class="score">{{ rankings[1].score }} 分</div>
          <div class="solved">解题数：{{ rankings[1].solved }}</div>
        </div>

        <!-- 第一名 -->
        <div v-if="rankings[0]" class="rank-card first">
          <div class="medal">
            <el-icon><Trophy /></el-icon>
            <span class="rank-num">1</span>
          </div>
          <el-avatar :size="100" :src="rankings[0].avatar" />
          <h3 class="username">{{ rankings[0].username }}</h3>
          <div class="score">{{ rankings[0].score }} 分</div>
          <div class="solved">解题数：{{ rankings[0].solved }}</div>
        </div>

        <!-- 第三名 -->
        <div v-if="rankings[2]" class="rank-card third">
          <div class="medal">
            <el-icon><Medal /></el-icon>
            <span class="rank-num">3</span>
          </div>
          <el-avatar :size="80" :src="rankings[2].avatar" />
          <h3 class="username">{{ rankings[2].username }}</h3>
          <div class="score">{{ rankings[2].score }} 分</div>
          <div class="solved">解题数：{{ rankings[2].solved }}</div>
        </div>
      </div>

      <!-- 排行榜表格 -->
      <el-card class="rank-table">
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
                <el-avatar :size="32" :src="row.avatar || defaultAvatar" />
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
                    {{ row.solved }}
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Trophy, Medal, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getRankingList } from '@/api/ranking'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(100)  // 固定为100，因为只显示前100名
const rankings = ref([])
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 计算最大页数
const getMaxPage = () => {
  return Math.ceil((total.value - 3) / pageSize.value)  // 减去前三名
}

// 获取标签类型
const getTagType = (difficulty) => {
  const types = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return types[difficulty]
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  // TODO: 调用API获取对应页的数据
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1  // 重置页码
  // TODO: 调用API获取数据
}

const getRankType = (rank) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return 'info'
}

const refreshRankings = async () => {
  loading.value = true
  try {
    const res = await getRankingList()
    if (res.code === 200) {
      // 转换API返回的数据格式以匹配现有的表格结构
      rankings.value = res.data.map(item => ({
        username: item.username,
        avatar: defaultAvatar,
        score: item.score,
        solved: item.solvedCount,
        solvedByDifficulty: {
          easy: Math.floor(item.solvedCount * 0.5),    // 假设50%是简单题
          medium: Math.floor(item.solvedCount * 0.3),  // 假设30%是中等题
          hard: Math.floor(item.solvedCount * 0.2)     // 假设20%是困难题
        }
      }))
    }
  } catch (error) {
    ElMessage.error('获取排行榜失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  // TODO: 获取排行榜数据
  refreshRankings()
})
</script>

<style lang="scss" scoped>
.leaderboard {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .page-title {
      margin: 0;
      font-size: 24px;
      color: #303133;
    }
  }

  .leaderboard-content {
    .top-three {
      display: flex;
      justify-content: center;
      align-items: flex-end;
      gap: 40px;
      margin-bottom: 40px;
      padding: 20px;

      .rank-card {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;

        &:hover {
          transform: translateY(-5px);
        }

        .medal {
          position: relative;
          margin-bottom: 16px;

          .el-icon {
            font-size: 32px;
            color: #ffd700;

            &.second {
              color: #c0c0c0;
            }

            &.third {
              color: #cd7f32;
            }
          }

          .rank-num {
            position: absolute;
            bottom: -8px;
            right: -8px;
            width: 20px;
            height: 20px;
            line-height: 20px;
            text-align: center;
            background: #fff;
            border-radius: 50%;
            font-size: 12px;
            font-weight: bold;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          }
        }

        .username {
          margin: 12px 0 8px;
          font-size: 18px;
          font-weight: 600;
          color: #303133;
        }

        .score {
          font-size: 24px;
          font-weight: bold;
          color: #409eff;
          margin-bottom: 8px;
        }

        .solved {
          font-size: 14px;
          color: #606266;
        }

        &.first {
          padding-top: 0;
          transform: scale(1.1);
          z-index: 2;

          &:hover {
            transform: scale(1.1) translateY(-5px);
          }

          .medal .el-icon {
            font-size: 40px;
          }
        }
      }
    }

    .rank-table {
      border-radius: 12px;
      
      :deep(.el-table) {
        border-radius: 12px;
        overflow: hidden;

        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .username {
            font-weight: 500;
          }
        }

        .rank {
          .el-tag {
            width: 40px;
            text-align: center;
          }
        }

        .score {
          font-weight: bold;
          color: #409EFF;
        }

        .solve-stats {
          display: flex;
          justify-content: center;
          gap: 8px;

          .el-tag {
            min-width: 40px;
            text-align: center;
          }
        }
      }

      .pagination-container {
        margin-top: 20px;
        padding: 10px 20px;
        display: flex;
        justify-content: flex-end;
        
        :deep(.el-pagination) {
          justify-content: flex-end;
          padding: 0;
        }
      }
    }
  }
}

// 响应式布局
@media screen and (max-width: 768px) {
  .leaderboard {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .page-title {
        font-size: 20px;
      }
    }

    .leaderboard-content {
      .top-three {
        gap: 20px;
        padding: 10px;

        .rank-card {
          padding: 16px;

          &.first {
            transform: scale(1.05);

            &:hover {
              transform: scale(1.05) translateY(-5px);
            }
          }

          .medal .el-icon {
            font-size: 28px;

            &.first {
              font-size: 32px;
            }
          }

          .username {
            font-size: 16px;
          }

          .score {
            font-size: 20px;
          }
        }
      }

      .rank-table {
        :deep(.el-table) {
          .solve-stats {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 4px;
          }
        }

        .pagination-container {
          padding: 10px;
          
          :deep(.el-pagination) {
            justify-content: center;
            flex-wrap: wrap;
            gap: 8px;
          }
        }
      }
    }
  }
}
</style> 