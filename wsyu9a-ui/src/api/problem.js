import request from '@/utils/request'

// 添加取消请求的支持
const controller = new AbortController()

// 用户端接口
export function getUserProblemList(params) {
  console.log('API调用参数:', params)  // 添加调试日志
  return request({
    url: '/api/problems',
    method: 'get',
    params
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
    url: '/api/admin/problem/list',
    method: 'get',
    params,
    signal: controller.signal
  })
}

// 获取题目详情
export function getProblemDetail(id) {
  return request({
    url: `/api/admin/problem/detail/${id}`,
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

// 获取最新题目列表
export function getLatestProblems(params) {
  return request({
    url: '/api/problems',
    method: 'get',
    params
  })
}

// 导出取消方法
export function cancelRequests() {
  controller.abort()
} 