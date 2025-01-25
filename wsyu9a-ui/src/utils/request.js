import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    console.log('[Request Interceptor] 请求配置', {
      url: config.url,
      method: config.method,
      data: config.data,
      headers: config.headers
    })

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
    console.error('[Request Interceptor] 请求错误', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 添加请求标识，避免重复处理
    if (response.config._handled) {
      console.log('[Response Interceptor] 响应已处理，跳过', {
        url: response.config.url,
        _handled: response.config._handled
      })
      return response.data
    }

    console.log('[Response Interceptor] 响应数据', {
      url: response.config.url,
      status: response.status,
      data: response.data,
      timestamp: Date.now()
    })

    // 标记该响应已被处理
    response.config._handled = true

    // 只处理HTTP错误，业务错误直接返回
    const res = response.data
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
      return Promise.reject(new Error('登录已过期'))
    }
    
    return res
  },
  error => {
    // 如果错误已经被处理过，直接返回
    if (error.config?._handled) {
      console.log('[Response Interceptor] 错误已处理，跳过', {
        url: error.config?.url,
        _handled: error.config?._handled
      })
      return Promise.reject(error)
    }

    // 标记错误已被处理
    if (error.config) {
      error.config._handled = true
    }

    // 只处理HTTP错误
    console.error('[Response Interceptor] 响应错误', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data,
      timestamp: Date.now()
    })

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
    }
    return Promise.reject(error)
  }
)

export default request 