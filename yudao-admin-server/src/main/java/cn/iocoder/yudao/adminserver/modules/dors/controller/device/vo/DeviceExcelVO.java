package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;


/**
 * 设备 Excel VO
 *
 * @author Bunco
 */
@Data
public class DeviceExcelVO {

    @ExcelProperty("主键（自增）")
    private Integer id;

    @ExcelProperty("所属房间")
    private Integer room;

    @ExcelProperty(value = "设备类型（PAD/ENCODER/DECODER）", converter = DictConvert.class)
    @DictFormat("dors_device_type") // TODO 代码优化：建议设置到对应的 XXXDictTypeConstants 枚举类中
    private DeviceType type;

    @ExcelProperty("设备序列号")
    private String serialNo;

    @ExcelProperty("设备MAC地址")
    private String deviceMac;

    @ExcelProperty("应用程序版本")
    private String appVersion;

    @ExcelProperty("SDK版本")
    private String sdkVersion;

    @ExcelProperty("系统版本")
    private String sysVersion;

    @ExcelProperty("最近一次上线IP地址")
    private String lastOnlineIp;

    @ExcelProperty("最近一次上线时间")
    private Date lastOnlineTime;

    @ExcelProperty("创建时间")
    private Date createTime;

    @ExcelProperty("备注")
    private String remarks;

}
