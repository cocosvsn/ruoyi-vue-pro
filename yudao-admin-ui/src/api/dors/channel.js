import request from '@/utils/request'

// 创建频道
export function createChannel(data) {
  return request({
    url: '/dors/channel/create',
    method: 'post',
    data: data
  })
}

// 更新频道
export function updateChannel(data) {
  return request({
    url: '/dors/channel/update',
    method: 'put',
    data: data
  })
}

// 删除频道
export function deleteChannel(id) {
  return request({
    url: '/dors/channel/delete?id=' + id,
    method: 'delete'
  })
}

// 获得频道
export function getChannel(id) {
  return request({
    url: '/dors/channel/get?id=' + id,
    method: 'get'
  })
}

// 获得频道分页
export function getChannelPage(query) {
  return request({
    url: '/dors/channel/page',
    method: 'get',
    params: query
  })
}

// 导出频道 Excel
export function exportChannelExcel(query) {
  return request({
    url: '/dors/channel/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
