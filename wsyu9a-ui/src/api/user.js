import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/api/user/regist',
    method: 'post',
    data: {
      username: data.username,
      email: data.email,
      password: data.password
    }
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/api/logout',
    method: 'post'
  })
}

// 重置密码方法
export function resetPassword(data) {
  return request({
    url: '/api/user/resetpwd',
    method: 'post',
    data: {
      email: data.email,
      oldPassword: data.oldPassword,
      newPassword: data.newPassword
    }
  })
}

// 获取用户统计信息
export function getUserStats() {
  return request({
    url: '/api/user/stats',
    method: 'get'
  })
}