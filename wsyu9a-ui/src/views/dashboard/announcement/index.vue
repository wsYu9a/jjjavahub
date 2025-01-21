<template>
  <div class="announcement-list">
    <div class="page-header">
      <h2 class="page-title">公告中心</h2>
      <div class="search-bar">
        <el-input
          v-model="queryParams.searchKey"
          placeholder="搜索公告标题"
          style="width: 300px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <div v-loading="loading" class="announcement-container">
      <!-- 公告列表 -->
      <div v-if="announcementList.length > 0" class="announcement-items">
        <div
          v-for="item in announcementList"
          :key="item.id"
          class="announcement-item"
          @click="handleView(item)"
        >
          <div class="item-header">
            <h3 class="title">
              <el-tag
                v-if="item.important"
                type="danger"
                size="small"
                class="important-tag"
                effect="dark"
              >
                重要
              </el-tag>
              {{ item.title }}
            </h3>
            <span class="time">{{ formatDateTime(item.createTime) }}</span>
          </div>
          <div class="item-content">
            {{ item.content.length > 200 ? item.content.substring(0, 200) + '...' : item.content }}
          </div>
          <div class="item-footer">
            <span class="publisher">
              <el-icon><User /></el-icon>
              {{ item.publisherName }}
            </span>
            <el-button type="primary" link>
              <el-icon><ArrowRight /></el-icon>
              查看详情
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else
        description="暂无公告"
      />

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="公告详情"
      width="800px"
      class="announcement-dialog"
    >
      <div v-if="currentAnnouncement" class="announcement-detail">
        <div class="detail-header">
          <h2>
            <el-tag
              v-if="currentAnnouncement.important"
              type="danger"
              size="small"
              class="important-tag"
            >
              重要
            </el-tag>
            {{ currentAnnouncement.title }}
          </h2>
          <div class="meta-info">
            <span>发布者：{{ currentAnnouncement.publisherName }}</span>
            <span>发布时间：{{ formatDateTime(currentAnnouncement.createTime) }}</span>
          </div>
        </div>
        <div class="detail-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User, ArrowRight } from '@element-plus/icons-vue'
import { getUserAnnouncementList, getUserAnnouncementDetail } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const announcementList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const currentAnnouncement = ref(null)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  searchKey: ''
})

// 获取公告列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserAnnouncementList(queryParams.value)
    if (res.code === 200) {
      announcementList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 查看公告详情
const handleView = async (row) => {
  try {
    const res = await getUserAnnouncementDetail(row.id)
    if (res.code === 200) {
      currentAnnouncement.value = res.data
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 搜索处理
const handleSearch = () => {
  queryParams.value.pageNum = 1
  getList()
}

// 分页处理
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.announcement-list {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;
    text-align: center;

    .page-title {
      color: #303133;
      font-size: 28px;
      font-weight: 600;
      margin-bottom: 20px;
    }

    .search-bar {
      display: flex;
      justify-content: center;

      :deep(.el-input) {
        .el-input__wrapper {
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
          border-radius: 20px;
          padding: 0 20px;
          
          &:hover {
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          }
        }
      }
    }
  }

  .announcement-container {
    background-color: transparent;
    border-radius: 8px;
    padding: 0;
  }

  .announcement-items {
    .announcement-item {
      padding: 24px;
      background: #fff;
      border-radius: 8px;
      margin-bottom: 16px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      border: 1px solid #ebeef5;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
      }

      .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .title {
          margin: 0;
          font-size: 20px;
          color: #303133;
          font-weight: 600;
          display: flex;
          align-items: center;

          .important-tag {
            margin-right: 12px;
            font-weight: normal;
          }
        }

        .time {
          color: #909399;
          font-size: 14px;
        }
      }

      .item-content {
        color: #606266;
        font-size: 15px;
        line-height: 1.8;
        margin-bottom: 16px;
        min-height: 54px;
      }

      .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #909399;
        font-size: 14px;

        .publisher {
          display: flex;
          align-items: center;
          gap: 4px;
          
          .el-icon {
            font-size: 16px;
          }
        }

        .el-button {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 14px;
          
          .el-icon {
            font-size: 16px;
          }
        }
      }
    }
  }
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    padding: 16px 24px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  }
}

.announcement-dialog {
  :deep(.el-dialog) {
    border-radius: 8px;
    overflow: hidden;
  }

  .detail-header {
    margin-bottom: 32px;

    h2 {
      margin: 0 0 20px;
      font-size: 26px;
      color: #303133;
      display: flex;
      align-items: center;

      .important-tag {
        margin-right: 12px;
      }
    }

    .meta-info {
      display: flex;
      justify-content: space-between;
      color: #909399;
      font-size: 14px;
      padding-bottom: 20px;
      border-bottom: 1px solid #ebeef5;
    }
  }

  .detail-content {
    color: #606266;
    font-size: 16px;
    line-height: 1.8;
    white-space: pre-wrap;
    padding: 0 12px;
  }
}

:deep(.el-dialog__body) {
  padding: 32px;
}

// 添加响应式布局
@media screen and (max-width: 768px) {
  .announcement-list {
    padding: 16px;

    .page-header {
      .page-title {
        font-size: 24px;
      }
    }

    .announcement-items {
      .announcement-item {
        padding: 16px;

        .item-header {
          .title {
            font-size: 18px;
          }
        }
      }
    }
  }
}
</style> 