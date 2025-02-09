import request from '@/utils/request'


export function getDashboardStats(params){
  return request({
    url: '/api/admin/dashboard/stats',
    method: 'get',
    params
  })
}

// 获取所有用户信息
export function getUserList(params) {
  return request({
    url: '/api/admin/getUserInfo',
    method: 'get',
    params
  })
}

// 设置用户状态
export function setUserStatus(data) {
  return request({
    url: '/api/admin/setUserStatus',
    method: 'post',
    data
  })
}

// 设置用户角色
export function setUserRole(data) {
  return request({
    url: '/api/admin/setUserRole',
    method: 'post',
    data
  })
}

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/admin/role/list',
    method: 'get',
    params
  })
}

// 添加角色
export function addRole(data) {
  return request({
    url: '/api/admin/role/add',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: '/api/admin/role/update',
    method: 'put',
    data
  })
}

// 更新角色状态
export function setRoleStatus(data) {
  return request({
    url: '/api/admin/role/status',
    method: 'put',
    data
  })
}

export function getRankingList(pageNum = 1, pageSize = 10) {
  return request({
    url: '/api/ranking',
    method: 'get',
    params: { pageNum, pageSize }
  });
} 

export function getUserStats() {
  return request({
    url: '/api/user/stats', // 确保这个 URL 是正确的
    method: 'get'
  });
}