import request from '@/utils/request'

export function getRankingList() {
  return request({
    url: '/api/ranking',
    method: 'get'
  })
} 