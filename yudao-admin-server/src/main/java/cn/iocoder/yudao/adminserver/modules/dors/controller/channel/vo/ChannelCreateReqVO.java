package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("频道创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChannelCreateReqVO extends ChannelBaseVO {

    @ApiModelProperty(value = "备注")
    private String remarks;

}
