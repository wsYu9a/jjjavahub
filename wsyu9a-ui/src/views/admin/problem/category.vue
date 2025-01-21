<template>
  <div class="category-manage">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleAdd">
            添加分类
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="categoryList"
        border
        style="width: 100%"
      >
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="分类名称" prop="name" />
        <el-table-column label="描述" prop="description" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(row)"
            >
              编辑
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
    </el-card>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加分类' : '编辑分类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = ref({
  id: null,
  name: '',
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 获取分类列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categoryList.value = res.data
    } else {
      ElMessage.error(res.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 添加/编辑操作
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: null,
    name: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const submitFn = dialogType.value === 'add' ? addCategory : updateCategory
    const res = await submitFn(form.value)
    if (res.code === 200) {
      ElMessage.success(`${dialogType.value === 'add' ? '添加' : '更新'}成功`)
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || `${dialogType.value === 'add' ? '添加' : '更新'}失败`)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(`${dialogType.value === 'add' ? '添加' : '更新'}失败`)
  }
}

// 删除操作
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该分类吗？删除后无法恢复！', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteCategory(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
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
.category-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style> 