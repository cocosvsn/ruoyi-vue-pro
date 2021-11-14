package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

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

    @ApiModelProperty(value = "所属设备", required = true)
    @NotNull(message = "所属设备不能为空")
    private Integer device;

    @ApiModelProperty(value = "设备频道编号", required = true)
    @NotNull(message = "设备频道编号不能为空")
    private Integer channelId;

    @ApiModelProperty(value = "频道类型（vi/usb/net/ndi/file/mix)", required = true)
    @NotNull(message = "频道类型（vi/usb/net/ndi/file/mix)不能为空")
    private String type;

    @ApiModelProperty(value = "频道名称", required = true)
    @NotNull(message = "频道名称不能为空")
    private String name;

    @ApiModelProperty(value = "频道JSON数据信息")
    private String jsonInfo;

    @ApiModelProperty(value = "是否显示")
    private Boolean display;
}
