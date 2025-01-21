import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 定义不需要token的接口列表
    const publicApis = [
      '/api/user/login',
      '/api/user/register',
      '/api/user/resetpwd'
    ]

    // 如果不是公共接口，才添加token
    if (!publicApis.includes(config.url)) {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }

    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 清除本地存储的用户信息
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('role')
        
        // 只在需要登录的页面显示过期提示
        const publicPages = ['/login', '/register', '/reset-password']
        if (!publicPages.includes(window.location.pathname)) {
          ElMessage.error('登录已过期，请重新登录')
          router.push('/login')
        }
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('Response error:', error)
    if (error.response?.status === 401) {
      // 清除本地存储的用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      
      // 只在需要登录的页面显示过期提示
      const publicPages = ['/login', '/register', '/reset-password']
      if (!publicPages.includes(window.location.pathname)) {
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
      }
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service 