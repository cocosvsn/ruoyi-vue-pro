package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.Data;

/**
 * 设备发现UDP报文数据结构
 * {
 *     "data": {
 *         "info": [
 *             "230.1",
 *             "230.2",
 *             "230.3",
 *             "230.4",
 *             "230.5",
 *             "230.6",
 *             "Net5",
 *             "手术室Mix"
 *         ],
 *         "type": "C3531D",
 *         "serialNo": "",
 *         "version": {
 *             "app": "1.0.0 build 20210125_707",
 *             "sdk": "1.0.0 build 20210201_17251",
 *             "sys": "1.0.0 build 20210201"
 *         }
 *     },
 *     "from": "88:63:4B:80:08:C1",
 *     "groupId": 0,
 *     "method": "hello",
 *     "seq": 3506,
 *     "to": "any",
 *     "type": "request"
 * }
 */
@Data
public class DeviceDiscovery {
    // 设备信息
    private Info data;
    // 来源设备MAC地址
    private String from;
    // 设备分组
    private Integer groupId;
    // 方法（暂时无用）
    private String method;
    // 报文序号
    private Integer seq;
    // 目标
    private String to;
    // 报文类型
    private String type;

    /**
     * 获取设备类型
     * @return
     */
    public DeviceType getDeviceType() {
        return this.getData().getType();
    }

    /**
     * 获取应用版本
     * @return
     */
    public String getAppVersion() {
        return this.getData().getVersion().getApp();
    }

    /**
     * 获取SDK版本
     * @return
     */
    public String getSdkVersion() {
        return this.getData().getVersion().getSdk();
    }

    /**
     * 获取系统版本
     * @return
     */
    public String getSysVersion() {
        return this.getData().getVersion().getSys();
    }
}
@Data
class Info {
    // 频道名称列表
    private String[] info;
    // 设备类型
    private DeviceType type;
    // 设备序列号（不一定所有设备都有）
    private String serialNo;
    // 设备版本信息
    private Version version;
}

@Data
class Version {
    // 应用版本
    private String app;
    // SDK版本
    private String sdk;
    // 系统版本
    private String sys;
}