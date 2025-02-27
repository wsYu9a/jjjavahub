import request from '@/utils/request'

// 添加取消请求的支持
const controller = new AbortController()

// 用户端接口
export function getUserProblemList(params) {
  const username = localStorage.getItem('username')  // 获取当前用户名
  console.log('API调用参数:', params)  // 添加调试日志
  return request({
    url: '/api/problems/user',  // 修改为新的API路径
    method: 'get',
    params: {
      ...params,
      username  // 添加用户名参数
    }
  })
}

export function getUserProblemDetail(id) {
  return request({
    url: `/api/problems/detail/${id}`,
    method: 'get'
  })
}

// 获取题目分类列表
export function getCategoryList() {
  return request({
    url: '/api/categories',
    method: 'get'
  })
}

// 获取题目列表
export function getProblemList(params) {
  console.log('API调用参数:', params) // 添加调试日志
  return request({
    url: '/api/problems',
    method: 'get',
    params
  })
}

// 获取题目详情
export function getProblemById(id) {
  return request({
    url: `/api/problems/${id}`,
    method: 'get'
  })
}

// 添加题目
export function addProblem(data) {
  return request({
    url: '/api/admin/problem/add',
    method: 'post',
    data
  })
}

// 更新题目
export function updateProblem(data) {
  return request({
    url: '/api/admin/problem/update',
    method: 'put',
    data
  })
}

// 删除题目
export function deleteProblem(id) {
  return request({
    url: `/api/admin/problem/delete/${id}`,
    method: 'delete'
  })
}

// 更新题目状态
export function updateProblemStatus(data) {
  return request({
    url: '/api/admin/problem/status',
    method: 'put',
    data
  })
}

// 上传docker-compose文件
export function uploadDockerCompose(data) {
  return request({
    url: '/api/admin/problem/upload/docker-compose',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data,
    // 添加这些配置以支持文件上传
    transformRequest: [function (data) {
      return data // 不转换 FormData
    }],
    timeout: 30000 // 增加超时时间
  })
}

// 上传 README 文件
export function uploadReadme(data) {
  return request({
    url: '/api/admin/problem/upload/readme',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data,
    timeout: 30000
  })
}

// 获取题目 README 内容
export function getProblemReadme(path) {
  return request({
    url: `/api/problems/readme/${btoa(path)}`,
    method: 'get'
  })
}

export function downloadFujian(path) {
  return request({
    url: `/api/problems/fujian/${btoa(encodeURIComponent(path))}`,
    method: 'get'
  })
}

// 获取最新题目列表
export function getLatestProblems(params) {
  return request({
    url: '/api/problems',
    method: 'get',
    params
  })
}

// 上传附件
export function uploadAttachments(data) {
  return request({
    url: '/api/admin/problem/upload/attachments',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data,
    transformRequest: [function (data) {
      return data
    }],
    timeout: 30000  // 30秒超时
  })
}

// 导出取消方法
export function cancelRequests() {
  controller.abort()
}

// 启动题目环境
export function startProblemEnv(problemId) {
  return request({
    url: `/api/problems/${problemId}/env/start`,
    method: 'post'
  })
}

// 停止题目环境
export function stopProblemEnv(problemId) {
  return request({
    url: `/api/problems/${problemId}/env/stop`,
    method: 'post',
    timeout: 60000  // 设置为1分钟
  })
}

// 获取环境状态
export function getProblemEnvStatus(problemId) {
  return request({
    url: `/api/problems/${problemId}/env/status`,
    method: 'get'
  })
}

let isSubmitting = false

export function submitFlag(problemId, flag) {
  if (isSubmitting) {
    console.log('[API] submitFlag 已有请求在进行中，忽略重复请求')
    return Promise.reject(new Error('请求正在处理中'))
  }

  console.log('[API] submitFlag 请求开始', { 
    problemId, 
    flag,
    timestamp: Date.now()
  })

  isSubmitting = true

  return request({
    url: `/api/problems/user/${problemId}/submit`,
    method: 'post',
    data: { flag },
    headers: {
      'X-Requested-With': 'XMLHttpRequest',
      'Cache-Control': 'no-cache'
    }
  }).then(res => {
    console.log('[API] submitFlag 请求成功', res)
    return res
  }).catch(err => {
    console.error('[API] submitFlag 请求失败', err)
    throw err
  }).finally(() => {
    isSubmitting = false
  })
}

// 获取热门题目
export function getHotProblems() {
  return request({
    url: '/api/problems/user/hot',  // 修改为新的路径
    method: 'get'
  })
} 