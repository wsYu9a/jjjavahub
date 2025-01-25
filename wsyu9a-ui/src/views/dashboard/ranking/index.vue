<template>
  <div class="ranking-page">
    <h2>排行榜</h2>
    
    <el-table 
      :data="rankings" 
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column 
        prop="rank" 
        label="排名" 
        width="100"
        align="center"
      >
        <template #default="{ row }">
          <div class="rank-cell">
            <el-tag 
              :type="getRankType(row.rank)"
              effect="dark"
            >
              {{ row.rank }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column 
        prop="username" 
        label="用户名"
        width="200"
      />
      
      <el-table-column 
        prop="score" 
        label="分数"
        width="150"
        align="center"
      >
        <template #default="{ row }">
          <span class="score">{{ row.score }}</span>
        </template>
      </el-table-column>
      
      <el-table-column 
        prop="solvedCount" 
        label="解题数"
        align="center"
      >
        <template #default="{ row }">
          <el-tag type="success">{{ row.solvedCount }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRankingList } from '@/api/ranking'

const rankings = ref([])
const loading = ref(false)

const getRankType = (rank) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return 'info'
}

const fetchRankings = async () => {
  loading.value = true
  try {
    const res = await getRankingList()
    if (res.code === 200) {
      rankings.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取排行榜失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRankings()
})
</script>

<style lang="scss" scoped>
.ranking-page {
  padding: 20px;
  
  .rank-cell {
    .el-tag {
      width: 40px;
      text-align: center;
    }
  }
  
  .score {
    font-weight: bold;
    color: #409EFF;
  }
}
</style> 