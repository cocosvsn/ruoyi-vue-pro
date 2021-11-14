import request from '@/utils/request'

// 创建手术视频
export function createOperationVideo(data) {
  return request({
    url: '/dors/operation-video/create',
    method: 'post',
    data: data
  })
}

// 更新手术视频
export function updateOperationVideo(data) {
  return request({
    url: '/dors/operation-video/update',
    method: 'put',
    data: data
  })
}

// 删除手术视频
export function deleteOperationVideo(id) {
  return request({
    url: '/dors/operation-video/delete?id=' + id,
    method: 'delete'
  })
}

// 获得手术视频
export function getOperationVideo(id) {
  return request({
    url: '/dors/operation-video/get?id=' + id,
    method: 'get'
  })
}

// 获得手术视频分页
export function getOperationVideoPage(query) {
  return request({
    url: '/dors/operation-video/page',
    method: 'get',
    params: query
  })
}

// 导出手术视频 Excel
export function exportOperationVideoExcel(query) {
  return request({
    url: '/dors/operation-video/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
