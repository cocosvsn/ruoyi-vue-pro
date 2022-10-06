package cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 直播 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class LiveBaseVO {

    @ApiModelProperty(value = "直播流名称")
    private String name;

    @ApiModelProperty(value = "封面图")
    private String icon;

    @ApiModelProperty(value = "地址")
    private String addr;

    @ApiModelProperty(value = "部门")
    private Integer deptId;

}
