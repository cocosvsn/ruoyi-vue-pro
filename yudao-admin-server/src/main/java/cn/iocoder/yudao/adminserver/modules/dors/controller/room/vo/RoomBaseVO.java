package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 房间 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class RoomBaseVO {

    @ApiModelProperty(value = "类型（手术室/会议室）", required = true)
    @NotNull(message = "类型（手术室/会议室）不能为空")
    private String type;

    @ApiModelProperty(value = "房间名称", required = true)
    @NotNull(message = "房间名称不能为空")
    private String name;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
