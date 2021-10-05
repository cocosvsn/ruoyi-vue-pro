package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 设备 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class DeviceBaseVO {

    @ApiModelProperty(value = "所属房间")
    private Integer room;

    @ApiModelProperty(value = "设备类型（PAD/ENCODER/DECODER）", required = true)
    @NotNull(message = "设备类型（PAD/ENCODER/DECODER）不能为空")
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

    @ApiModelProperty(value = "最近一次上线时间", required = true)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date lastOnlineTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
