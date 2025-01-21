<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h2>用户登录</h2>
        <p>欢迎回来</p>
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
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <div class="form-actions">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
          <el-link type="primary" @click="router.push('/resetpwd')">忘记密码？</el-link>
        </div>
        
        <el-button
          type="primary"
          :loading="loading"
          class="auth-button"
          @click="handleSubmit"
        >
          登录
        </el-button>
      </el-form>
      
      <div class="auth-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await userStore.login({
      username: form.value.username,
      password: form.value.password,
      remember: form.value.remember
    })
    
    if (res.code === 200) {
      ElMessage.success('登录成功')
      // 根据返回的角色进行跳转
      router.push(res.data.role === 'ADMIN' ? '/admin' : '/dashboard')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/auth.scss';
</style> 