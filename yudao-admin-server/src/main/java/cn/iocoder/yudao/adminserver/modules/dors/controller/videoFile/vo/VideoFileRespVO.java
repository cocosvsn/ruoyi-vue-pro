package cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

@ApiModel("手术视频文件 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VideoFileRespVO extends VideoFileBaseVO {

    @ApiModelProperty(value = "主键（自增）", required = true)
    private Integer id;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

}
