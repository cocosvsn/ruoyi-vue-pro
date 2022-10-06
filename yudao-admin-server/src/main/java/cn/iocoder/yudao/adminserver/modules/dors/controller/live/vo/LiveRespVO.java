package cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("直播 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LiveRespVO extends LiveBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

}
