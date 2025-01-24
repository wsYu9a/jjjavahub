import request from '@/utils/request'

// 获取所有容器
export function getContainers() {
  return request({
    url: '/api/problems/0/env/admin/containers',
    method: 'get'
  })
}

// 停止容器
export function stopContainer(problemId) {
  return request({
    url: `/api/problems/${problemId}/env/stop`,
    method: 'post'
  })
}

// 延长容器时间
export function extendContainer(problemId, minutes) {
  return request({
    url: `/api/problems/${problemId}/env/admin/extend`,
    method: 'post',
    params: { minutes }
  })
}