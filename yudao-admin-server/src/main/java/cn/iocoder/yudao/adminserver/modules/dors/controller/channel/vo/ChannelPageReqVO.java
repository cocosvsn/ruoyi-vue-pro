package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamDirectionType;
import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel("频道分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChannelPageReqVO extends PageParam {

    @ApiModelProperty(value = "所属房间")
    private Integer room;

    @ApiModelProperty(value = "频道ID")
    private Integer channelId;

    @ApiModelProperty(value = "频道类型（vi/usb/net/ndi/file/mix)")
    private String type;

    @ApiModelProperty(value = "流类型（编码器输出流/网络输入流)")
    private StreamDirectionType streamType;

    @ApiModelProperty(value = "频道名称")
    private String name;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始创建时间")
    private Date beginCreateTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;

}
