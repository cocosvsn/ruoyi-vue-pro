package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

@ApiModel("房间更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoomUpdateReqVO extends RoomBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    @NotNull(message = "主键（自增）不能为空")
    private Integer id;

}
