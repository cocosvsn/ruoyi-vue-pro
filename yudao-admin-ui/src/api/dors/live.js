import request from '@/utils/request'

// 创建直播
export function createLive(data) {
  return request({
    url: '/dors/live/create',
    method: 'post',
    data: data
  })
}

// 更新直播
export function updateLive(data) {
  return request({
    url: '/dors/live/update',
    method: 'put',
    data: data
  })
}

// 删除直播
export function deleteLive(id) {
  return request({
    url: '/dors/live/delete?id=' + id,
    method: 'delete'
  })
}

// 获得直播
export function getLive(id) {
  return request({
    url: '/dors/live/get?id=' + id,
    method: 'get'
  })
}

// 获得直播分页
export function getLivePage(query) {
  return request({
    url: '/dors/live/page',
    method: 'get',
    params: query
  })
}

// 导出直播 Excel
export function exportLiveExcel(query) {
  return request({
    url: '/dors/live/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
