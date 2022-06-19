package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceUpdateReqVO;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("房间更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoomUpdateReqVO extends RoomBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    @NotNull(message = "主键（自增）不能为空")
    private Integer id;

    @ApiModelProperty(value = "编码器")
    private List<DeviceUpdateReqVO> encoderDevices;

    @ApiModelProperty(value = "解码器")
    private List<DeviceUpdateReqVO> decoderDevices;

    @ApiModelProperty(value = "IPC")
    private List<DeviceUpdateReqVO> ipcDevices;

    @ApiModelProperty(value = "TV")
    private List<DeviceUpdateReqVO> tvDevices;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
