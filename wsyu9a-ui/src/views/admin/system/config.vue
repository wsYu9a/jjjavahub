<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Docker 配置</span>
          <div>
            <el-button type="primary" @click="handleSave">保存配置</el-button>
            <el-button @click="resetToDefault">恢复默认</el-button>
          </div>
        </div>
      </template>

      <el-form ref="formRef" :model="form" label-width="140px" :rules="rules">
        <el-form-item label="Docker API地址" prop="dockerApi">
          <el-input v-model="form.dockerApi" placeholder="例如: tcp://localhost:2376" />
        </el-form-item>

        <el-form-item label="镜像仓库地址" prop="dockerRegistry">
          <el-input v-model="form.dockerRegistry" placeholder="例如: http://hub-mirror.c.163.com" />
        </el-form-item>

        <el-form-item label="容器最小端口" prop="dockerMinPort">
          <el-input-number v-model="form.dockerMinPort" :min="1024" :max="65535" />
        </el-form-item>

        <el-form-item label="容器最大端口" prop="dockerMaxPort">
          <el-input-number v-model="form.dockerMaxPort" :min="1024" :max="65535" />
        </el-form-item>

        <el-form-item label="容器超时时间(秒)" prop="dockerTime">
          <el-input-number v-model="form.dockerTime" :min="300" :max="86400" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfig, getDefaultConfig, updateConfig } from '@/api/config'

const formRef = ref(null)
const form = ref({
  id: 2,
  dockerApi: 'tcp://localhost:2376',
  dockerRegistry: 'http://hub-mirror.c.163.com',
  dockerMaxPort: 50000,
  dockerMinPort: 40000,
  dockerTime: 7200
})

const rules = {
  dockerApi: [{ required: true, message: '请输入Docker API地址', trigger: 'blur' }],
  dockerRegistry: [{ required: true, message: '请输入镜像仓库地址', trigger: 'blur' }],
  dockerMinPort: [{ required: true, message: '请输入容器最小端口', trigger: 'blur' }],
  dockerMaxPort: [{ required: true, message: '请输入容器最大端口', trigger: 'blur' }],
  dockerTime: [{ required: true, message: '请输入容器超时时间', trigger: 'blur' }]
}

const loadConfig = async () => {
  console.log('Loading config...')
  try {
    const res = await getConfig()
    console.log('Config loaded:', res)
    if (res.code === 200 && res.data) {
      form.value = res.data
    } else {
      console.warn('Invalid response:', res)
      ElMessage.error(res.message || '加载配置失败')
    }
  } catch (error) {
    console.error('Load config error:', error)
    ElMessage.error(error.message || '加载配置失败')
  }
}

const resetToDefault = async () => {
  try {
    const res = await getDefaultConfig()
    if (res.code === 200 && res.data) {
      form.value = { ...res.data, id: 2 }
      ElMessage.success('已恢复默认配置')
    }
  } catch (error) {
    ElMessage.error('恢复默认配置失败')
  }
}

const handleSave = async () => {
  console.log('Saving config...')
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (valid) {
      console.log('Form data to save:', form.value)
      const res = await updateConfig(form.value)
      console.log('Save response:', res)
      if (res.code === 200) {
        ElMessage.success('保存成功')
        await loadConfig()
      } else {
        console.warn('Invalid save response:', res)
        ElMessage.error(res.message || '保存失败')
      }
    }
  } catch (error) {
    console.error('Save config error:', error)
    ElMessage.error(error.message || '保存失败')
  }
}

// 添加组件卸载前的清理
onBeforeUnmount(() => {
  formRef.value = null
})

onMounted(() => {
  loadConfig().catch(error => {
    console.error('Failed to load config:', error)
    ElMessage.error('加载配置失败')
  })
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-container {
  padding: 20px;
}
</style> 