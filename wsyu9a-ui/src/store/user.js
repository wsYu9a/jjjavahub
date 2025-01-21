import { defineStore } from 'pinia'
import { login, logout } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: {
      username: localStorage.getItem('username') || '',
      role: localStorage.getItem('role') || ''
    },
    rememberMe: localStorage.getItem('rememberMe') === 'true'
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username,
    role: (state) => state.userInfo?.role
  },

  actions: {
    async login(loginData) {
      try {
        const res = await login(loginData)
        this.token = res.data.token
        this.userInfo = {
          username: res.data.username,
          role: res.data.role
        }
        
        // 存储认证信息
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('username', res.data.username)
        localStorage.setItem('role', res.data.role)
        
        if (loginData.remember) {
          localStorage.setItem('rememberMe', 'true')
          localStorage.setItem('rememberedUsername', loginData.username)
        } else {
          localStorage.removeItem('rememberMe')
          localStorage.removeItem('rememberedUsername')
        }
        
        return res
      } catch (error) {
        this.clearUserInfo()
        throw error
      }
    },

    async logout() {
      try {
        await logout()
        this.clearUserInfo()
        return true
      } catch (error) {
        console.error('登出失败:', error)
        throw error
      }
    },

    clearUserInfo() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      // 保留记住我的选项
      if (!this.rememberMe) {
        localStorage.removeItem('rememberedUsername')
      }
    }
  }
}) 