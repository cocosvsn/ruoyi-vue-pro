import request from '@/utils/request'

// 创建手术视频文件
export function createVideoFile(data) {
  return request({
    url: '/dors/video-file/create',
    method: 'post',
    data: data
  })
}

// 更新手术视频文件
export function updateVideoFile(data) {
  return request({
    url: '/dors/video-file/update',
    method: 'put',
    data: data
  })
}

// 删除手术视频文件
export function deleteVideoFile(id) {
  return request({
    url: '/dors/video-file/delete?id=' + id,
    method: 'delete'
  })
}

// 获得手术视频文件
export function getVideoFile(id) {
  return request({
    url: '/dors/video-file/get?id=' + id,
    method: 'get'
  })
}

// 获得手术视频文件分页
export function getVideoFilePage(query) {
  return request({
    url: '/dors/video-file/page',
    method: 'get',
    params: query
  })
}

// 导出手术视频文件 Excel
export function exportVideoFileExcel(query) {
  return request({
    url: '/dors/video-file/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
