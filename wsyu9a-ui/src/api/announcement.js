import request from '@/utils/request'

// 用户端接口
export function getUserAnnouncementList(params) {
  return request({
    url: '/api/announcements',
    method: 'get',
    params
  })
}

export function getUserAnnouncementDetail(id) {
  return request({
    url: `/api/announcements/detail/${id}`,
    method: 'get'
  })
}

// 获取公告列表
export function getAnnouncementList(params) {
  return request({
    url: '/api/admin/announcement/list',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return request({
    url: `/api/admin/announcement/detail/${id}`,
    method: 'get'
  })
}

// 添加公告
export function addAnnouncement(data) {
  return request({
    url: '/api/admin/announcement/add',
    method: 'post',
    data
  })
}

// 更新公告
export function updateAnnouncement(data) {
  return request({
    url: '/api/admin/announcement/update',
    method: 'put',
    data
  })
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({
    url: `/api/admin/announcement/delete/${id}`,
    method: 'delete'
  })
} 