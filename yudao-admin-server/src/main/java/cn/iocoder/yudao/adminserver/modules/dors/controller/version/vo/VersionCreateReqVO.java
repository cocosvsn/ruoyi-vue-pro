package cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("应用版本创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VersionCreateReqVO extends VersionBaseVO {

    @ApiModelProperty(value = "更新日志")
    private String changeLog;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
