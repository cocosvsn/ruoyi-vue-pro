package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("手术视频更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperationVideoUpdateReqVO extends OperationVideoBaseVO {
    @ApiModelProperty(value = "主键（自增）", required = true)
    @NotNull(message = "主键（自增）不能为空")
    private Integer id;
}
