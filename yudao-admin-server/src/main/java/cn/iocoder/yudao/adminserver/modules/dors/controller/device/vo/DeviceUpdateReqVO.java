package cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo;

import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.ChannelUpdateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("设备更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeviceUpdateReqVO extends DeviceBaseVO {

    @ApiModelProperty(value = "主键（自增）")
    private Integer id;

    @ApiModelProperty(value = "设备类型（ENCODER/DECODER/IPC/TV）")
    private DeviceType type;

    @ApiModelProperty(value = "设备生厂商")
    private String manufacturer;

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

    @ApiModelProperty(value = "设备登陆密码")
    private String loginPass;

    @ApiModelProperty(value = "通道列表")
    private List<ChannelUpdateReqVO> channels;
}
