package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel("房间绑定设备 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoomBindDeviceReqVO extends RoomBaseVO {

    @ApiModelProperty(value = "房间编号", required = true)
    @NotNull(message = "房间编号不能为空")
    private Integer roomId;

    @ApiModelProperty(value = "操控面板", required = true)
    @NotNull(message = "操控面板不能为空")
    private Integer padId;

    @ApiModelProperty(value = "编码器", required = true)
    @NotNull(message = "编码器不能为空")
    private Integer encoderId;
}
