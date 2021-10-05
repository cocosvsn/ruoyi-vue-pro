import request from '@/utils/request'

// 创建房间
export function createRoom(data) {
  return request({
    url: '/dors/room/create',
    method: 'post',
    data: data
  })
}

// 更新房间
export function updateRoom(data) {
  return request({
    url: '/dors/room/update',
    method: 'put',
    data: data
  })
}

// 房间绑定设备
export function bindDevice(data) {
  return request({
    url: '/dors/room/bind-device',
    method: 'put',
    data: data
  })
}

// 删除房间
export function deleteRoom(id) {
  return request({
    url: '/dors/room/delete?id=' + id,
    method: 'delete'
  })
}

// 获得房间
export function getRoom(id) {
  return request({
    url: '/dors/room/get?id=' + id,
    method: 'get'
  })
}

// 获得房间列表
export function listRoom(query) {
  return request({
    url: '/dors/room/list',
    method: 'get',
    params: query
  })
}

// 获得房间分页
export function getRoomPage(query) {
  return request({
    url: '/dors/room/page',
    method: 'get',
    params: query
  })
}

// 导出房间 Excel
export function exportRoomExcel(query) {
  return request({
    url: '/dors/room/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
