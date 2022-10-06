package cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("直播更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LiveUpdateReqVO extends LiveBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    @NotNull(message = "主键（自增）不能为空")
    private Integer id;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
