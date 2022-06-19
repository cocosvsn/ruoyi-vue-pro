import request from '@/utils/request'

// 创建设备
export function createDevice(data) {
  return request({
    url: '/dors/device/create',
    method: 'post',
    data: data
  })
}

// 更新设备
export function updateDevice(data) {
  return request({
    url: '/dors/device/update',
    method: 'put',
    data: data
  })
}

// 删除设备
export function deleteDevice(id) {
  return request({
    url: '/dors/device/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备
export function getDevice(id) {
  return request({
    url: '/dors/device/get?id=' + id,
    method: 'get'
  })
}

// 重新搜索并获取设备信息
export function rediscover() {
  return request({
    url: '/dors/device/rediscover',
    method: 'put'
  })
}

// 获取服务器磁盘使用统计
export function usage() {
  return request({
    url: '/dors/device/usage',
    method: 'get'
  })
}

// 获得设备分页
export function getDevicePage(query) {
  return request({
    url: '/dors/device/page',
    method: 'get',
    params: query
  })
}

// 导出设备 Excel
export function exportDeviceExcel(query) {
  return request({
    url: '/dors/device/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 导出设备 Excel
export function configDecoderDevice(device) {
  if('LINKPI' === device.type) { // 灵派
    configDecoderLinkPi(device);
  } else if('SHXIT' === device.type) { // 示见
    configDecoderShxit(device);
  }
}

/** 
 * 灵派编码器配置 
 * 配置流程：
 * 1. 获取灵派编码器配置。
 * 2. 获取网络输入类型的接口配置。
 * 3. 比较灵派编码器支持的网络通道数量和当前解码器配置的通道数量， 取小者。
 * 4. 将灵派编码器网络通道的输入地址一一配置为解码器配置的通道URL。
 * 5. 将灵派编码器配置保存。
 */
function configDecoderLinkPi(device) {}

/** 示见编码器配置 */
function configDecoderShxit(device) {}