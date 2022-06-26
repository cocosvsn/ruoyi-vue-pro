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
  if('LINKPI' === device.manufacturer) { // 灵派
    return configDecoderLinkPi(device);
  } else if('SHXIT' === device.manufacturer) { // 示见
    return configDecoderShxit(device);
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
function configDecoderLinkPi(device) {
  let query = {'ip': device.ip, 'type': device.manufacturer}
  let configParams = {
    "id": 1,
    "jsonrpc": "2.0",
    "method": "enc.update",
    "params": []
  }
  return request({
    url: '/dors/device/get-device-config',
    method: 'get',
    params: query
  }).then(response => {
    let linkpiConfig = JSON.parse(response.data);
    let configDecoderChannelLength = device.channels.length;
    let currChannelIndex = 0;
    // 修改网络输入配置
    linkpiConfig.forEach(function (n, i) {
      if ("net" == n.type && currChannelIndex < configDecoderChannelLength) {
        linkpiConfig[i].enable = true;
        linkpiConfig[i].enable2 = true;
        linkpiConfig[i].net.path = device.channels[currChannelIndex].url;
        linkpiConfig[i].net.protocol = 'udp';
        linkpiConfig[i].net.decodeV = true;
        linkpiConfig[i].net.decodeA = true;
        currChannelIndex ++;
      } else if("net" == n.type) {
        linkpiConfig[i].enable = false;
        linkpiConfig[i].enable2 = false;
        linkpiConfig[i].net.path = '';
        linkpiConfig[i].net.protocol = 'tcp';
        linkpiConfig[i].net.decodeV = false;
        linkpiConfig[i].net.decodeA = false;
      }
    })
    
    configParams.params.push(JSON.stringify(linkpiConfig))
    return request({
      url: 'dors/device/config-device',
      method: 'post',
      params: query,
      data: configParams
    })
  })
}

/** 示见编码器配置 */
function configDecoderShxit(device) {
  let query = {'ip': device.ip, 'type': device.manufacturer}
  let configParams = [
    {
      "WindowIndex": 0,
      "AccessSourceID": 32768,
      "CachingTime": -1,
      "BuildInSource": {
        "Address": "rtsp://192.168.1.229:554/ch1_main"
      }
    },
    {
      "WindowIndex": 1,
      "AccessSourceID": 98304,
      "CachingTime": -1,
      "BuildInSource": {
        "Address": "rtsp://192.168.1.229:554/ch1_main"
      }
    },
    {
      "WindowIndex": 2,
      "AccessSourceID": 163840,
      "CachingTime": -1,
      "BuildInSource": {
        "Address": "rtsp://192.168.1.229:554/ch1_main"
      }
    },
    {
      "WindowIndex": 3,
      "AccessSourceID": -1,
      "CachingTime": -1,
      "BuildInSource": {
        "Address": "rtsp://192.168.1.229:554/ch1_main"
      }
    },
    {
      "WindowIndex": 4,
      "AccessSourceID": -1,
      "CachingTime": -1,
      "BuildInSource": {
        "Address": ""
      }
    }
  ]

  let channel_num = Math.min(4, device.channels.length);
  // 示见解码器只配置4个通道
  for(let i = 0; i < channel_num; i ++) {
    configParams[i].AccessSourceID = -1;
    configParams[i].BuildInSource.Address = device.channels[i].url;
  }
  console.log(configParams);
  return request({
    url: "dors/device/config-device",
    method: 'post',
    params: query,
    data: configParams
  });
}