package cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 手术视频文件 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class VideoFileBaseVO {

    @ApiModelProperty(value = "关联手术")
    private Integer operationVideo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "文件类型")
    private String contentType;

    @ApiModelProperty(value = "文件大小")
    private Integer fileSize;

    @ApiModelProperty(value = "相对路径")
    private String relativePath;

}
