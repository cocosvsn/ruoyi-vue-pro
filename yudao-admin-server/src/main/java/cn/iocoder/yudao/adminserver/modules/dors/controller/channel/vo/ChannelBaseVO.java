package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamDirectionType;
import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamProtocol;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 频道 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ChannelBaseVO {

    @ApiModelProperty(value = "所属房间", required = true)
    @NotNull(message = "所属房间不能为空")
    private Integer room;

    @ApiModelProperty(value = "所属设备", required = true)
    @NotNull(message = "所属设备不能为空")
    private Integer device;

    @ApiModelProperty(value = "设备频道编号")
    private Integer channelId;

    @ApiModelProperty(value = "频道类型（vi/usb/net/ndi/file/mix)")
    private String type;

    @ApiModelProperty(value = "流协议（SRT/RTSP）")
    private StreamProtocol streamProtocol;

    @ApiModelProperty(value = "流方向类型（编码器输出流/网络输入流)")
    private StreamDirectionType streamType;

    @ApiModelProperty(value = "频道名称", required = true)
    @NotNull(message = "频道名称不能为空")
    private String name;

    @ApiModelProperty(value = "通道图标")
    private String icon;

    @ApiModelProperty(value = "流地址")
    private String url;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "频道JSON数据信息")
    private String jsonInfo;

    @ApiModelProperty(value = "是否显示")
    private Boolean display;

    @ApiModelProperty(value = "连接至矩阵的端口")
    private String matrixPort;

    @ApiModelProperty(value = "串口")
    private String serialPort;

    @ApiModelProperty(value = "是否绑定摄像机")
    private Boolean isCamera;

    @ApiModelProperty(value = "摄像机云台控制串口信息")
    private String cameraSerialPort;
}
