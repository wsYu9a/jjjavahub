import request from '@/utils/request'

export function getLatestSolveRecords() {
  return request({
    url: '/api/solve-records/latest',
    method: 'get'
  })
} 