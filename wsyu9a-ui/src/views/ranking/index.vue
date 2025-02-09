<template>
  <div class="ranking-list">
    <el-table :data="rankingList" v-loading="loading" style="width: 100%">
      <el-table-column prop="rank" label="排名" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="score" label="分数" />
      <el-table-column prop="solved_count" label="解决题目数" />
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRankingList } from '@/api/ranking'

const rankingList = ref([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchRankingList = async () => {
  loading.value = true
  try {
    const response = await getRankingList(currentPage.value, pageSize.value)
    if (response.code === 200) {
      rankingList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    console.error('获取排行榜失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchRankingList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchRankingList()
}

onMounted(() => {
  fetchRankingList()
})
</script> 