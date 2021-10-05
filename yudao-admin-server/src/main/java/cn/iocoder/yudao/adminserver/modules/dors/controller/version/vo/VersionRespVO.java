package cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("应用版本 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VersionRespVO extends VersionBaseVO {

    @ApiModelProperty(value = "编号（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "浏览次数")
    private Integer hits;

    @ApiModelProperty(value = "下载次数")
    private Integer dlCount;

    @ApiModelProperty(value = "更新时间", required = true)
    private Date updateTime;

}
