package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceCreateReqVO;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("房间创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoomCreateReqVO extends RoomBaseVO {

    @ApiModelProperty(value = "操控面板")
    private DeviceCreateReqVO pad;

    @ApiModelProperty(value = "编码器")
    private List<DeviceCreateReqVO> encoderDevices;

    @ApiModelProperty(value = "解码器")
    private List<DeviceCreateReqVO> decoderDevices;

    @ApiModelProperty(value = "IPC")
    private List<DeviceCreateReqVO> ipcDevices;

    @ApiModelProperty(value = "TV")
    private List<DeviceCreateReqVO> tvDevices;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
