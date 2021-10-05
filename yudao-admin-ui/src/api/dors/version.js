import request from '@/utils/request'

// 创建房间
export function createVersion(data) {
  return request({
    url: '/dors/version/create',
    method: 'post',
    data: data
  })
}

// 更新房间
export function updateVersion(data) {
  return request({
    url: '/dors/version/update',
    method: 'put',
    data: data
  })
}

// 删除房间
export function deleteVersion(id) {
  return request({
    url: '/dors/version/delete?id=' + id,
    method: 'delete'
  })
}

// 获得房间
export function getVersion(id) {
  return request({
    url: '/dors/version/get?id=' + id,
    method: 'get'
  })
}

// 获得房间列表
export function listVersion(query) {
  return request({
    url: '/dors/version/list',
    method: 'get',
    params: query
  })
}

// 获得房间分页
export function getVersionPage(query) {
  return request({
    url: '/dors/version/page',
    method: 'get',
    params: query
  })
}

// 导出房间 Excel
export function exportVersionExcel(query) {
  return request({
    url: '/dors/version/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
