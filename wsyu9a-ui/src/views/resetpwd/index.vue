<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h2>重置密码</h2>
        <p>请输入邮箱和密码信息</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="auth-form"
      >
        <el-form-item prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
          >
            <template #prefix>
              <el-icon class="input-icon"><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          >
            <template #prefix>
              <el-icon class="input-icon"><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          >
            <template #prefix>
              <el-icon class="input-icon"><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!form.email || !form.oldPassword || !form.newPassword || !form.confirmPassword || !formRef?.validate"
          class="auth-button"
          @click="handleSubmit"
        >
          重置密码
        </el-button>
        
        <div class="auth-footer">
          <el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, Lock, Key } from '@element-plus/icons-vue'
import { resetPassword } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  email: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    await resetPassword({
      email: form.email,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    
    ElMessage.success('密码重置成功，请登录')
    router.push('/login')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

// 添加表单验证状态
const formValid = ref(false)

// 监听表单变化
watch([form], () => {
  if (formRef.value) {
    formRef.value.validate((valid) => {
      formValid.value = valid
    })
  }
}, { deep: true })
</script>

<style lang="scss" scoped>
@import '@/styles/auth.scss';
</style> 