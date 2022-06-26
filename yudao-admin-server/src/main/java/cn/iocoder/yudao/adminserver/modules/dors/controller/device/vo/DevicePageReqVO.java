package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel("设备分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DevicePageReqVO extends PageParam {

    @ApiModelProperty(value = "所属房间")
    private Integer room;

    @ApiModelProperty(value = "设备类型（ENCODER/DECODER/IPC/TV）")
    private DeviceType type;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备IP地址")
    private String ip;

    @ApiModelProperty(value = "设备MAC地址")
    private String mac;

    @ApiModelProperty(value = "设备通道数量")
    private Integer channelCount;

    @ApiModelProperty(value = "连接至矩阵的端口")
    private String matrixPort;

    @ApiModelProperty(value = "设备序列号")
    private String serialNo;

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
