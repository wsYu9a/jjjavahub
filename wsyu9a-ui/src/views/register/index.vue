<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h2>用户注册</h2>
        <p>创建您的账号</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="auth-form"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
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
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          >
            <template #prefix>
              <el-icon class="input-icon"><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <div class="form-actions">
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!form.username || !form.email || !form.password || !form.confirmPassword || !formRef?.validate"
            class="auth-button"
            @click="handleSubmit"
          >
            注册
          </el-button>
        </div>
        
        <div class="auth-footer">
          <el-link type="primary" @click="$router.push('/login')">已有账号？返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Message, Lock, Key } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度必须在6-20个字符之间'))
  } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含大小写字��和数字'))
  } else {
    // 当密码变化时，验证确认密码
    if (form.confirmPassword) {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

let submitTimer = null
const handleSubmit = async () => {
  if (!formRef.value || loading.value) return
  
  // 清除之前的定时器
  if (submitTimer) {
    clearTimeout(submitTimer)
  }
  
  // 设置防抖
  submitTimer = setTimeout(async () => {
    try {
      await formRef.value.validate()
      loading.value = true
      
      console.log('发送注册请求:', {
        username: form.username,
        email: form.email,
        password: form.password
      })

      const response = await register({
        username: form.username,
        email: form.email,
        password: form.password
      })
      
      console.log('注册响应:', response)
      
      ElMessage({
        type: 'success',
        message: '注册成功，即将跳转到登录页面',
        duration: 2000
      })
      
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } catch (error) {
      console.error('注册错误:', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else if (error.message) {
        ElMessage.error(error.message)
      } else {
        ElMessage.error('注册失败，请稍后重试')
      }
    } finally {
      loading.value = false
    }
  }, 300)
}

// 组件卸载时清除定时器
onUnmounted(() => {
  if (submitTimer) {
    clearTimeout(submitTimer)
  }
})

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