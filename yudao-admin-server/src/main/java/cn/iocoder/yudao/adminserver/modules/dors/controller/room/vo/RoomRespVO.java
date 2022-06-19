package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceCreateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceRespVO;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("房间 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoomRespVO extends RoomBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "录制状态")
    private Boolean record;

    @ApiModelProperty(value = "编码器")
    private List<DeviceRespVO> encoderDevices;

    @ApiModelProperty(value = "解码器")
    private List<DeviceRespVO> decoderDevices;

    @ApiModelProperty(value = "IPC")
    private List<DeviceRespVO> ipcDevices;

    @ApiModelProperty(value = "TV")
    private List<DeviceRespVO> tvDevices;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
