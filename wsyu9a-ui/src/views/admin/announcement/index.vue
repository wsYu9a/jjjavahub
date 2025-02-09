<template>
  <div class="announcement-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <div class="header-operations">
            <el-input
              v-model="queryParams.searchKey"
              placeholder="搜索公告标题/内容"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
              发布公告
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="announcementList"
        border
        style="width: 100%"
      >
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="标题" prop="title" min-width="200" />
        <el-table-column label="发布者" prop="publisherName" width="120" />
        <el-table-column label="重要性" width="100">
          <template #default="{ row }">
            <el-tag :type="row.important ? 'danger' : 'info'">
              {{ row.important ? '重要' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">
              {{ row.enabled ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="primary" link @click="handlePreview(row)">
              预览
            </el-button>
            <el-button type="danger" @click.stop="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布公告' : '编辑公告'"
      width="800px"
      @close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        
        <el-form-item label="重要性" prop="important">
          <el-switch
            v-model="form.important"
            active-text="重要"
            inactive-text="普通"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="enabled">
          <el-switch
            v-model="form.enabled"
            active-text="发布"
            inactive-text="草稿"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submitLoading" 
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="公告预览"
      width="800px"
    >
      <div class="preview-content">
        <h2>{{ previewData.title }}</h2>
        <div class="meta-info">
          <span>发布者：{{ previewData.publisherName }}</span>
          <span>发布时间：{{ formatDateTime(previewData.createTime) }}</span>
        </div>
        <div class="content">{{ previewData.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnnouncementList, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  searchKey: ''
})

// 数据列表
const announcementList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const form = ref({
  title: '',
  content: '',
  important: false,
  enabled: true
})

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

// 获取列表数据
const getList = async () => {
  try {
    loading.value = true
    const res = await getAnnouncementList(queryParams.value)
    if (res.code === 200) {
      announcementList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
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

// 新增操作
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    title: '',
    content: '',
    important: false,
    enabled: true
  }
  dialogVisible.value = true
  // 等待DOM更新后重置表单
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields()
    }
  })
}

// 编辑操作
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = {
    id: row.id,
    title: row.title,
    content: row.content,
    important: row.important,
    enabled: row.enabled
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 如果已经在提交中，直接返回
  if (submitLoading.value) {
    return
  }

  try {
    // 表单验证
    await formRef.value.validate()
    
    // 设置提交状态
    submitLoading.value = true
    
    const submitFn = dialogType.value === 'add' ? addAnnouncement : updateAnnouncement
    const res = await submitFn(form.value)
    
    if (res.code === 200) {
      ElMessage.success(`${dialogType.value === 'add' ? '发布' : '更新'}成功`)
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || `${dialogType.value === 'add' ? '发布' : '更新'}失败`)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || `${dialogType.value === 'add' ? '发布' : '更新'}失败`)
  } finally {
    // 重置提交状态
    submitLoading.value = false
  }
}

// 预览相关
const previewVisible = ref(false)
const previewData = ref(null)

const handlePreview = (row) => {
  previewData.value = row
  previewVisible.value = true
}

// 添加对话框关闭处理
const handleClose = () => {
  formRef.value?.resetFields()
  form.value = {
    title: '',
    content: '',
    important: false,
    enabled: true
  }
  submitLoading.value = false
}

// 删除公告
const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该公告吗？删除后无法恢复！', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAnnouncement(id)
      ElMessage.success('删除成功')
      getList() // 重新获取列表
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}


onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.announcement-manage {
  padding: 16px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-operations {
    display: flex;
    align-items: center;
  }
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.preview-content {
  padding: 20px;
  
  h2 {
    margin: 0 0 16px;
    text-align: center;
  }
  
  .meta-info {
    color: #666;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
  }
  
  .content {
    line-height: 1.6;
    white-space: pre-wrap;
  }
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style> 