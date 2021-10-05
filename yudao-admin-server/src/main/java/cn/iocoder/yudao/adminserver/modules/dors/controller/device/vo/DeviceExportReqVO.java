package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel(value = "设备 Excel 导出 Request VO", description = "参数和 DevicePageReqVO 是一致的")
@Data
public class DeviceExportReqVO {

    @ApiModelProperty(value = "所属房间")
    private Integer room;

    @ApiModelProperty(value = "设备类型（PAD/ENCODER/DECODER）")
    private DeviceType type;

    @ApiModelProperty(value = "设备序列号")
    private String serialNo;

    @ApiModelProperty(value = "设备MAC地址")
    private String deviceMac;

    @ApiModelProperty(value = "应用程序版本")
    private String appVersion;

    @ApiModelProperty(value = "SDK版本")
    private String sdkVersion;

    @ApiModelProperty(value = "系统版本")
    private String sysVersion;

    @ApiModelProperty(value = "最近一次上线IP地址")
    private String lastOnlineIp;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始最近一次上线时间")
    private Date beginLastOnlineTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束最近一次上线时间")
    private Date endLastOnlineTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始创建时间")
    private Date beginCreateTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
