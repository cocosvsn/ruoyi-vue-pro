package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("频道 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChannelRespVO extends ChannelBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date updateTime;

}
