package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("手术视频 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperationVideoRespVO extends OperationVideoBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

}
