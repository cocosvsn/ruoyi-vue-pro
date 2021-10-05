package cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("应用版本更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VersionUpdateReqVO extends VersionBaseVO {

    @ApiModelProperty(value = "编号（自增）", required = true)
    @NotNull(message = "编号（自增）不能为空")
    private Integer id;

    @ApiModelProperty(value = "下载地址")
    private String url;

    @ApiModelProperty(value = "校验码")
    private String checksum;

    @ApiModelProperty(value = "大小")
    private Long size;

    @ApiModelProperty(value = "更新日志")
    private String changeLog;

    @ApiModelProperty(value = "浏览次数")
    private Integer hits;

    @ApiModelProperty(value = "下载次数")
    private Integer dlCount;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
