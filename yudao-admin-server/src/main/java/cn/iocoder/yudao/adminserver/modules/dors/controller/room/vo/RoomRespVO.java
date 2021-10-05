package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

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

    @ApiModelProperty(value = "绑定的设备列表", required = false)
    private List<DeviceRespVO> devices;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

}
