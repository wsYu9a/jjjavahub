<template>
  <div class="problem-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-button type="primary" @click="handleAdd">
              添加题目
            </el-button>
            <el-select 
              v-model="queryParams.categoryId" 
              placeholder="选择分类"
              clearable
              @change="handleSearch"
            >
              <el-option
                v-for="item in categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </div>
          <el-input
            v-model="queryParams.searchKey"
            placeholder="搜索题目标题/简述"
            style="width: 200px"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="problemList"
        border
        style="width: 100%"
      >
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="标题" prop="title" min-width="200" />
        <el-table-column label="分类" prop="categoryName" width="120" />
        <el-table-column label="难度" prop="difficulty" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">
              {{ row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="积分" prop="score" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              type="success" 
              link
              @click="handleUpload(row)"
            >
              上传Docker
            </el-button>
            <el-button 
              type="danger" 
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加题目' : '编辑题目'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="选择难度">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="Flag" prop="flag">
          <el-input v-model="form.flag" />
        </el-form-item>
        <el-form-item label="简述" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="详细说明" prop="detail">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :limit="1"
            accept=".md"
            :on-change="handleReadmeChange"
            :on-remove="handleReadmeRemove"
            :file-list="readmeFiles"
            :before-upload="beforeUpload"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将 README.md 拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 markdown 文件
              </div>
            </template>
          </el-upload>

          <div v-if="readmeFiles.length > 0" class="assets-upload">
            <el-upload
              action="#"
              :auto-upload="false"
              :limit="10"
              accept="image/*"
              :on-change="handleAssetChange"
              :on-remove="handleAssetRemove"
              :file-list="assetFiles"
              :before-upload="beforeUpload"
            >
              <el-button type="primary">添加图片资源</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持 jpg、png、gif 格式图片
                </div>
              </template>
            </el-upload>
            
            <div class="upload-actions">
              <el-button 
                type="success" 
                :loading="uploadStatus.readme"
                :disabled="!readmeFiles.length || uploadStatus.readme"
                @click="submitReadme"
              >
                {{ uploadStatus.readme ? '上传中...' : '立即上传' }}
              </el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submitting"
          @click="submitForm"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 上传Docker Compose对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传Docker Compose文件"
      width="500px"
    >
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :limit="1"
        accept=".yml,.yaml"
        :on-change="handleFileChange"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 docker-compose.yml 文件
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="uploadStatus.docker"
          :disabled="!currentFile || uploadStatus.docker"
          @click="submitUpload"
        >
          {{ uploadStatus.docker ? '上传中...' : '确定上传' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, UploadFilled } from '@element-plus/icons-vue'
import * as problemApi from '@/api/problem'  // 使用命名空间导入
import { getCategoryList } from '@/api/category'
import request from '@/utils/request'

// 修改上传相关的状态管理
const readmeFiles = ref([])
const assetFiles = ref([])
const uploading = ref(false)
const uploadStatus = ref({
  readme: false,
  docker: false
})

const loading = ref(false)
const problemList = ref([])
const categories = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const uploadDialogVisible = ref(false)
const currentProblemId = ref(null)
const currentFile = ref(null)
const formRef = ref(null)
const submitting = ref(false)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  searchKey: '',
  categoryId: null
})

const form = ref({
  id: null,
  title: '',
  summary: '',
  detail: '',
  flag: '',
  score: 10,
  difficulty: 'MEDIUM',
  categoryId: null,
  enabled: true,
  dockerComposePath: null
})

const rules = {
  title: [
    { required: true, message: '请输入题目标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  score: [
    { required: true, message: '请输入积分', trigger: 'blur' },
    { type: 'number', message: '积分必须为数字', trigger: 'blur' }
  ],
  flag: [
    { required: true, message: '请输入flag', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入题目简述', trigger: 'blur' }
  ]
}

// 获取题目列表
const getList = async () => {
  loading.value = true
  try {
    const res = await problemApi.getProblemList(queryParams.value)
    if (res.code === 200) {
      problemList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
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

// 添加/编辑操作
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: null,
    title: '',
    summary: '',
    detail: '',
    flag: '',
    score: 10,
    difficulty: 'MEDIUM',
    categoryId: null,
    enabled: true,
    dockerComposePath: null
  }
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogType.value = 'edit'
  try {
    const res = await problemApi.getProblemDetail(row.id)
    form.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error('获取题目详情失败:', error)
  }
}

// 修改文件上传相关的处理方法
const beforeUpload = () => {
  return false // 阻止自动上传
}

const handleReadmeChange = (file) => {
  if (!file.raw.name.endsWith('.md')) {
    ElMessage.error('只能上传 markdown 文件')
    return false
  }
  // 直接替换文件列表，而不是追加
  readmeFiles.value = [file]
  return false // 阻止自动上传
}

const handleAssetChange = (file) => {
  if (!file.raw.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  // 检查是否已存在相同文件
  const exists = assetFiles.value.some(f => f.uid === file.uid)
  if (!exists) {
    assetFiles.value.push(file)
  }
  return false // 阻止自动上传
}

const handleReadmeRemove = () => {
  readmeFiles.value = []
  form.value.detail = ''
}

const handleAssetRemove = (file) => {
  const index = assetFiles.value.findIndex(f => f.uid === file.uid)
  if (index !== -1) {
    assetFiles.value.splice(index, 1)
  }
}

// 修改提交 README 的方法
const submitReadme = async () => {
  if (readmeFiles.value.length === 0) {
    ElMessage.warning('请选择README文件')
    return
  }

  try {
    uploadStatus.value.readme = true
    const formData = new FormData()
    formData.append('file', readmeFiles.value[0].raw)
    
    if (assetFiles.value.length > 0) {
      assetFiles.value.forEach(file => {
        formData.append('assets', file.raw)
      })
    }

    console.log('准备上传文件:', {
      mainFile: readmeFiles.value[0].name,
      assetsCount: assetFiles.value.length
    })

    const res = await problemApi.uploadReadme(formData)
    if (res.code === 200) {
      form.value.readmePath = res.data
      form.value.detail = res.data
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploadStatus.value.readme = false
  }
}

// 修改 Docker Compose 上传相关的方法
const submitUpload = async () => {
  if (!currentFile.value) {
    ElMessage.warning('请选择文件')
    return
  }

  if (uploadStatus.value.docker) {
    return
  }

  try {
    uploadStatus.value.docker = true
    const formData = new FormData()
    formData.append('file', currentFile.value.raw)
    formData.append('problemId', currentProblemId.value)
    
    const res = await problemApi.uploadDockerCompose(formData)
    if (res.code === 200) {
      ElMessage.success('上传成功')
      uploadDialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploadStatus.value.docker = false
  }
}

// 修改表单提交方法
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (!form.value.readmePath) {
      ElMessage.warning('请先上传题目说明文件')
      return
    }

    const data = {
      ...form.value,
      title: form.value.title.trim(),
      summary: form.value.summary.trim(),
      flag: form.value.flag.trim(),
      categoryId: form.value.categoryId,
      difficulty: form.value.difficulty,
      score: form.value.score
    }

    let res
    if (dialogType.value === 'add') {
      res = await problemApi.addProblem(data)
    } else {
      res = await problemApi.updateProblem(data)
    }

    if (res.code === 200) {
      ElMessage.success(`${dialogType.value === 'add' ? '添加' : '更新'}成功`)
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || `${dialogType.value === 'add' ? '添加' : '更新'}失败`)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || `${dialogType.value === 'add' ? '添加' : '更新'}失败`)
  } finally {
    loading.value = false
  }
}

// 删除操作
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该题目吗？删除后无法恢复！', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await problemApi.deleteProblem(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    await problemApi.updateProblemStatus({
      problemId: row.id,
      enabled: row.enabled
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.enabled = !row.enabled
    console.error('状态更新失败:', error)
  }
}

// 上传Docker Compose
const handleUpload = (row) => {
  currentProblemId.value = row.id
  currentFile.value = null
  uploadDialogVisible.value = true
}

const handleFileChange = (file) => {
  // 检查文件类型
  if (!file.raw.name.endsWith('.yml') && !file.raw.name.endsWith('.yaml')) {
    ElMessage.error('只能上传 yml 或 yaml 文件')
    return false
  }
  currentFile.value = file
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

// 监听查询参数变化
watch(
  () => queryParams.value,
  () => {
    console.log('查询参数变化:', queryParams.value)
    currentPage.value = 1
    getList()
  },
  { deep: true }
)

// 重置表单时也要清空文件列表
const resetForm = () => {
  form.value = {
    id: null,
    title: '',
    summary: '',
    detail: '',
    readmePath: '',
    flag: '',
    score: 10,
    difficulty: 'MEDIUM',
    categoryId: null,
    enabled: true,
    dockerComposePath: null
  }
  // 同时重置文件列表
  readmeFiles.value = []
  assetFiles.value = []
}

// 在关闭对话框时重置表单
watch(dialogVisible, (val) => {
  if (!val) {
    resetForm()
  }
})

// 直接在组件中定义 API 方法
const uploadReadme = (data) => {
  return request({
    url: '/api/admin/problem/upload/readme',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data,
    transformRequest: [function (data) {
      return data
    }],
    timeout: 30000
  })
}

onMounted(() => {
  getList()
  getCategories()
})
</script>

<style lang="scss" scoped>
.problem-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .left {
    display: flex;
    gap: 16px;
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}

.upload-demo {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
  margin-bottom: 16px;
  
  :deep(.el-upload) {
    width: 100%;
  }
  
  :deep(.el-upload-dragger) {
    width: 100%;
    height: 180px;
  }
}

.assets-upload {
  margin-top: 16px;
  padding: 16px;
  border: 1px dashed #e6e6e6;
  border-radius: 4px;
  
  :deep(.el-upload-list) {
    margin-top: 8px;
  }
}

.upload-actions {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style> 