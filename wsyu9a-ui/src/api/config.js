import request from '@/utils/request'

export function getConfig() {
  return request({
    url: '/api/admin/config',
    method: 'get',
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export function getDefaultConfig() {
  return request({
    url: '/api/admin/config/default',
    method: 'get',
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export function updateConfig(data) {
  return request({
    url: '/api/admin/config',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: data
  })
} 