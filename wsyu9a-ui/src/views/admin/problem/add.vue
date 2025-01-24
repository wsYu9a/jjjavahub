<template>
  <div class="problem-add">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span class="title">添加题目</span>
          <div class="actions">
            <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
            <el-button @click="resetForm">重置</el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="problem-form"
      >
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入题目标题" style="max-width: 500px" />
          </el-form-item>

          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 200px">
              <el-option
                v-for="item in categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="难度" prop="difficulty">
            <el-select v-model="form.difficulty" placeholder="选择难度" style="width: 200px">
              <el-option label="简单" value="EASY" />
              <el-option label="中等" value="MEDIUM" />
              <el-option label="困难" value="HARD" />
            </el-select>
          </el-form-item>

          <el-form-item label="积分" prop="score">
            <el-input-number v-model="form.score" :min="1" :max="100" style="width: 180px" />
          </el-form-item>

          <el-form-item label="Flag" prop="flag">
            <el-input v-model="form.flag" placeholder="请输入flag" style="max-width: 400px">
              <template #append>
                <el-button @click="generateRandomFlag">生成随机</el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="简述" prop="summary">
            <el-input
              v-model="form.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入题目简述"
              style="max-width: 600px"
            />
          </el-form-item>
        </div>

        <div class="form-section">
          <h3 class="section-title">Docker配置</h3>
          <el-form-item label="配置文件">
            <div class="upload-container">
              <el-upload
                class="upload-component"
                action="#"
                :auto-upload="false"
                :limit="1"
                accept=".yml,.yaml"
                :on-change="handleDockerFileChange"
                :on-remove="handleDockerFileRemove"
                :file-list="dockerFiles"
                :before-upload="beforeUpload"
              >
                <template #trigger>
                  <el-button type="primary">
                    <el-icon><Upload /></el-icon>
                    <span>选择Docker Compose文件</span>
                  </el-button>
                </template>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 yml 或 yaml 文件
                  </div>
                </template>
              </el-upload>
              <div v-if="dockerFiles.length > 0" class="upload-actions">
                <el-button 
                  type="success" 
                  :loading="uploadStatus.docker"
                  :disabled="!dockerFiles.length || uploadStatus.docker"
                  @click="submitDockerFile"
                >
                  {{ uploadStatus.docker ? '上传中...' : '上传Docker配置' }}
                </el-button>
              </div>
            </div>
          </el-form-item>
        </div>

        <div class="form-section">
          <h3 class="section-title">题目说明</h3>
          <el-form-item label="说明文件" prop="detail">
            <div class="upload-container">
              <el-upload
                class="upload-component"
                action="#"
                :auto-upload="false"
                :limit="1"
                accept=".md"
                :on-change="handleReadmeChange"
                :on-remove="handleReadmeRemove"
                :file-list="readmeFiles"
                :before-upload="beforeUpload"
              >
                <template #trigger>
                  <el-button type="primary">
                    <el-icon><Upload /></el-icon>
                    <span>选择说明文件</span>
                  </el-button>
                </template>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 markdown 文件
                  </div>
                </template>
              </el-upload>
              <div v-if="readmeFiles.length > 0" class="upload-actions">
                <el-button 
                  type="success" 
                  :loading="uploadStatus.readme"
                  :disabled="!readmeFiles.length || uploadStatus.readme"
                  @click="submitReadme"
                >
                  {{ uploadStatus.readme ? '上传中...' : '上传说明文件' }}
                </el-button>
              </div>
            </div>
          </el-form-item>
        </div>

        <div class="form-section">
          <h3 class="section-title">附件管理</h3>
          <el-form-item label="题目附件">
            <div class="upload-container">
              <el-upload
                class="upload-component"
                action="#"
                :auto-upload="false"
                :limit="5"
                accept=".txt,.zip,.jar"
                :on-change="handleAttachmentChange"
                :on-remove="handleAttachmentRemove"
                :file-list="attachmentFiles"
                :before-upload="beforeUpload"
              >
                <template #trigger>
                  <el-button type="primary">
                    <el-icon><Upload /></el-icon>
                    <span>选择附件</span>
                  </el-button>
                </template>
                <template #tip>
                  <div class="el-upload__tip">
                    支持上传 txt、zip、jar 格式文件，单个文件不超过10MB
                  </div>
                </template>
              </el-upload>
              <div v-if="attachmentFiles.length > 0" class="upload-actions">
                <el-button 
                  type="success" 
                  :loading="uploadStatus.attachment"
                  :disabled="!attachmentFiles.length || uploadStatus.attachment"
                  @click="submitAttachments"
                >
                  {{ uploadStatus.attachment ? '上传中...' : '上传附件' }}
                </el-button>
              </div>
            </div>
          </el-form-item>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import * as problemApi from '@/api/problem'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const categories = ref([])

// 上传相关的状态管理
const readmeFiles = ref([])
const attachmentFiles = ref([])
const dockerFiles = ref([])
const uploadStatus = ref({
  readme: false,
  docker: false,
  attachment: false
})

const form = ref({
  title: '',
  summary: '',
  detail: '',
  flag: '',
  score: 10,
  difficulty: 'MEDIUM',
  categoryId: null,
  enabled: true,
  dockerComposePath: null,
  attachmentPath: null,
  readmePath: null
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

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 文件上传相关方法
const beforeUpload = () => false // 阻止自动上传

const handleReadmeChange = (file) => {
  if (!file.raw.name.endsWith('.md')) {
    ElMessage.error('只能上传 markdown 文件')
    return false
  }
  readmeFiles.value = [file]
  return false
}

const handleReadmeRemove = () => {
  readmeFiles.value = []
  form.value.detail = ''
}

const handleDockerFileChange = (file) => {
  if (!file.raw.name.endsWith('.yml') && !file.raw.name.endsWith('.yaml')) {
    ElMessage.error('只能上传 yml 或 yaml 文件')
    return false
  }
  dockerFiles.value = [file]
  return false
}

const handleDockerFileRemove = () => {
  dockerFiles.value = []
  form.value.dockerComposePath = null
}

const handleAttachmentChange = (file) => {
  const allowedTypes = ['.txt', '.zip', '.jar']
  const extension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
  
  if (!allowedTypes.includes(extension)) {
    ElMessage.error('只能上传txt、zip、jar格式的文件')
    return false
  }
  
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  
  const exists = attachmentFiles.value.some(f => f.uid === file.uid)
  if (!exists) {
    attachmentFiles.value.push(file)
  }
  return false
}

const handleAttachmentRemove = (file) => {
  const index = attachmentFiles.value.findIndex(f => f.uid === file.uid)
  if (index !== -1) {
    attachmentFiles.value.splice(index, 1)
  }
}

// 提交文件的方法
const submitReadme = async () => {
  if (readmeFiles.value.length === 0) {
    ElMessage.warning('请选择README文件')
    return
  }

  try {
    uploadStatus.value.readme = true
    const formData = new FormData()
    formData.append('file', readmeFiles.value[0].raw)
    
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

const submitDockerFile = async () => {
  if (dockerFiles.value.length === 0) {
    ElMessage.warning('请选择Docker Compose文件')
    return
  }

  try {
    uploadStatus.value.docker = true
    const formData = new FormData()
    formData.append('file', dockerFiles.value[0].raw)
    
    const res = await problemApi.uploadDockerCompose(formData)
    if (res.code === 200) {
      form.value.dockerComposePath = res.data
      ElMessage.success('Docker配置上传成功')
    } else {
      ElMessage.error(res.message || 'Docker配置上传失败')
    }
  } catch (error) {
    console.error('Docker配置上传失败:', error)
    ElMessage.error('Docker配置上传失败')
  } finally {
    uploadStatus.value.docker = false
  }
}

const submitAttachments = async () => {
  if (attachmentFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的附件')
    return
  }

  try {
    uploadStatus.value.attachment = true
    const formData = new FormData()
    
    attachmentFiles.value.forEach(file => {
      formData.append('files', file.raw)
    })

    const res = await problemApi.uploadAttachments(formData)
    if (res.code === 200) {
      form.value.attachmentPath = res.data
      ElMessage.success('附件上传成功')
    } else {
      ElMessage.error(res.message || '附件上传失败')
    }
  } catch (error) {
    console.error('附件上传失败:', error)
    ElMessage.error('附件上传失败')
  } finally {
    uploadStatus.value.attachment = false
  }
}

// 生成随机flag
const generateRandomString = (length) => {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length))
  }
  return result
}

const generateRandomFlag = () => {
  const randomString = generateRandomString(16)
  form.value.flag = `flag{${randomString}}`
}

// 表单提交
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (!form.value.readmePath) {
      ElMessage.warning('请先上传题目说明文件')
      return
    }

    if (!form.value.dockerComposePath) {
      ElMessage.warning('请先上传Docker配置文件')
      return
    }

    const data = {
      title: form.value.title.trim(),
      categoryId: form.value.categoryId,
      difficulty: form.value.difficulty,
      score: form.value.score,
      flag: form.value.flag.trim(),
      summary: form.value.summary.trim(),
      detail: form.value.readmePath,
      dockerComposePath: form.value.dockerComposePath,
      attachmentPath: form.value.attachmentPath,
      enabled: true
    }

    const res = await problemApi.addProblem(data)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      router.push('/admin/problem/list')
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '添加失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    title: '',
    summary: '',
    detail: '',
    readmePath: '',
    flag: '',
    score: 10,
    difficulty: 'MEDIUM',
    categoryId: null,
    enabled: true,
    dockerComposePath: null,
    attachmentPath: null
  }
  readmeFiles.value = []
  attachmentFiles.value = []
  dockerFiles.value = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  getCategories()
})

// 确保组件被正确导出
defineOptions({
  name: 'ProblemAdd'
})
</script>

<style lang="scss" scoped>
.problem-add {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 40px);
}

.form-card {
  max-width: 1200px;
  margin: 0 auto;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
    }
    
    .actions {
      display: flex;
      gap: 12px;
    }
  }
}

.problem-form {
  margin-top: 20px;
}

.form-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin: 0 0 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
}

.upload-container {
  background-color: #fafafa;
  border-radius: 4px;
  padding: 20px;
  border: 1px solid #ebeef5;
  height: 300px;
  width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;

  .upload-component {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
    
    :deep(.el-upload) {
      width: 100%;
      display: flex;
      justify-content: center;
      padding: 30px 0;
    }
    
    :deep(.el-upload-list) {
      margin-top: 12px;
      flex: 1;
      min-height: 120px;
      overflow-y: auto;
    }
    
    :deep(.el-button) {
      display: flex;
      align-items: center;
      gap: 8px;
      min-width: 160px;
      
      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .upload-actions {
    margin-top: 16px;
    display: flex;
    justify-content: center;
    gap: 12px;
    padding: 8px 0;
  }

  .el-upload__tip {
    color: #909399;
    font-size: 12px;
    margin-top: 8px;
    line-height: 1.4;
    text-align: center;
    padding: 4px 0;
  }
}

.w-full {
  width: 100%;
}

// 响应式布局
@media (max-width: 768px) {
  .form-card {
    margin: 0;
  }
  
  .form-section {
    padding: 16px;
  }
  
  .el-form-item {
    margin-bottom: 18px;
  }
}
</style> 