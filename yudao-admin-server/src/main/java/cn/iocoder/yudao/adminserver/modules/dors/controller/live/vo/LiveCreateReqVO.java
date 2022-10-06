package cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("直播创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LiveCreateReqVO extends LiveBaseVO {

    @ApiModelProperty(value = "备注")
    private String remarks;

}
