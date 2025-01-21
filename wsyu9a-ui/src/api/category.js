import request from '@/utils/request'

// 获取分类列表
export function getCategoryList() {
  return request({
    url: '/api/admin/category/list',
    method: 'get'
  })
}

// 添加分类
export function addCategory(data) {
  return request({
    url: '/api/admin/category/add',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(data) {
  return request({
    url: '/api/admin/category/update',
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/api/admin/category/delete/${id}`,
    method: 'delete'
  })
} 