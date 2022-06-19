package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.ChannelCreateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("设备创建 Request VO")
@Data
@ToString(callSuper = true)
public class DeviceCreateReqVO {

    @ApiModelProperty(value = "设备类型（ENCODER/DECODER/IPC/TV）")
    private DeviceType type;

    @ApiModelProperty(value = "设备生厂商")
    private String manufacturer;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备IP地址")
    private String ip;

    @ApiModelProperty(value = "设备通道数量")
    private Integer channelCount;

    @ApiModelProperty(value = "连接至矩阵的端口")
    private String matrixPort;

    @ApiModelProperty(value = "通道列表")
    private List<ChannelCreateReqVO> channels;
}
