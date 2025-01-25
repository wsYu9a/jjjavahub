<template>
  <div class="problem-list">
    <div class="page-header">
      <h2 class="page-title">题目列表</h2>
      <div class="filter-bar">
        <el-select
          v-model="queryParams.categoryId"
          placeholder="选择分类"
          clearable
          class="filter-item"
          @change="handleSearch"
        >
          <el-option
            label="全部"
            value=""
          />
          <el-option
            v-for="item in categories"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.difficulty"
          placeholder="选择难度"
          clearable
          class="filter-item"
          @change="handleSearch"
        >
          <el-option
            label="全部"
            value=""
          />
          <el-option label="简单" value="EASY" />
          <el-option label="中等" value="MEDIUM" />
          <el-option label="困难" value="HARD" />
        </el-select>
        <el-input
          v-model="queryParams.searchKey"
          placeholder="搜索题目"
          class="filter-item"
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

    <div v-loading="loading" class="problem-container">
      <!-- 题目列表 -->
      <div v-if="problemList.length > 0" class="problem-items">
        <div
          v-for="item in problemList"
          :key="item.id"
          class="problem-item"
          @click="handleView(item)"
        >
          <div class="item-header">
            <h3 class="title">
              {{ item.title }}
            </h3>
            <el-tag
              :type="getDifficultyType(item.difficulty)"
              size="small"
              effect="dark"
            >
              {{ item.difficulty === 'EASY' ? '简单' : 
                 item.difficulty === 'MEDIUM' ? '中等' : '困难' }}
            </el-tag>
          </div>
          <div class="item-summary">{{ item.summary }}</div>
          <div class="item-content">
            <div class="category">
              <el-icon><Collection /></el-icon>
              {{ item.categoryName }}
            </div>
            <div class="score">
              <el-icon><Trophy /></el-icon>
              积分：{{ item.score }}
            </div>
            <div class="status">
              <el-tag
                :type="item.solved ? 'success' : 'info'"
                size="small"
                effect="plain"
              >
                {{ item.solved ? '已解决' : '未解决' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else
        description="暂无题目"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Collection, Trophy } from '@element-plus/icons-vue'
import { getUserProblemList } from '@/api/problem'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const problemList = ref([])
const categories = ref([])
const total = ref(0)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  searchKey: '',
  categoryId: '',
  difficulty: ''
})

// 获取题目列表
const getList = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: queryParams.value.pageNum,
      pageSize: queryParams.value.pageSize,
      searchKey: queryParams.value.searchKey || undefined,
      categoryId: queryParams.value.categoryId || undefined,
      difficulty: queryParams.value.difficulty || undefined
    }
    
    const res = await getUserProblemList(params)
    if (res.code === 200) {
      problemList.value = res.data.records
      total.value = res.data.total
      
      // 打印日志，检查数据
      console.log('题目列表:', problemList.value)
      console.log('solved状态:', problemList.value.map(p => p.solved))
    }
  } catch (error) {
    console.error('获取题目列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 查看题目详情
const handleView = (row) => {
  router.push(`/problem/${row.id}`)
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
  getCategories()
})
</script>

<style lang="scss" scoped>
.problem-list {
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

    .filter-bar {
      display: flex;
      justify-content: center;
      gap: 16px;

      .filter-item {
        width: 200px;
      }

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

  .problem-container {
    background-color: transparent;
    border-radius: 8px;
    padding: 0;
  }

  .problem-items {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin: 0 auto;
    max-width: 1400px;

    .problem-item {
      display: flex;
      flex-direction: column;
      padding: 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      border: 1px solid #ebeef5;
      cursor: pointer;
      transition: all 0.3s ease;
      min-height: 165px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
      }

      .item-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;

        .title {
          margin: 0;
          font-size: 15px;
          color: #303133;
          font-weight: 600;
          line-height: 1.5;
          flex: 1;
          margin-right: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;

          .problem-id {
            color: #909399;
            font-weight: normal;
            margin-right: 8px;
          }
        }
      }

      .item-summary {
        color: #606266;
        font-size: 13px;
        line-height: 1.8;
        margin-bottom: 12px;
        flex: 1;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .item-content {
        display: flex;
        align-items: center;
        gap: 12px;
        color: #606266;
        font-size: 12px;
        margin-top: 12px;
        flex-wrap: wrap;

        .category,
        .score {
          display: flex;
          align-items: center;
          gap: 4px;

          .el-icon {
            font-size: 14px;
          }
        }

        .status {
          margin-left: auto;
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

// 响应式布局
@media screen and (max-width: 1400px) {
  .problem-list .problem-items {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1200px) {
  .problem-list .problem-items {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .problem-list {
    padding: 16px;

    .page-header {
      .page-title {
        font-size: 24px;
      }

      .filter-bar {
        flex-direction: column;
        align-items: center;

        .filter-item {
          width: 100%;
          max-width: 300px;
        }
      }
    }

    .problem-items {
      grid-template-columns: 1fr;
      gap: 16px;

      .problem-item {
        padding: 16px;

        .item-content {
          gap: 12px;

          .status {
            margin-left: 0;
            width: 100%;
            display: flex;
            justify-content: flex-end;
          }
        }
      }
    }
  }
}
</style> 