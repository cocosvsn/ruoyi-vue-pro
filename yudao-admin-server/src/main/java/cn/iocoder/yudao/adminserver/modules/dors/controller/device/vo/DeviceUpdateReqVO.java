package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("设备更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeviceUpdateReqVO extends DeviceBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    @NotNull(message = "主键（自增）不能为空")
    private Integer id;

}
