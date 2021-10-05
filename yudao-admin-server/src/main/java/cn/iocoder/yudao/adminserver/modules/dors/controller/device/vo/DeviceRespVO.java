package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("设备 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeviceRespVO extends DeviceBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

}
