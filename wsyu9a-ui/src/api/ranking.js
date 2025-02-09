import request from '@/utils/request'

export function getRankingList(pageNum = 1, pageSize = 10) {
  return request({
    url: '/api/ranking',
    method: 'get',
    params: { pageNum, pageSize }
  });
} 