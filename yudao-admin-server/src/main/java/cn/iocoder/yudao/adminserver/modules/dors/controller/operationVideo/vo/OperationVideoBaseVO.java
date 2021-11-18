package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 手术视频 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class OperationVideoBaseVO {

    @ApiModelProperty(value = "所属手术室")
    private Integer room;

    @ApiModelProperty(value = "手术名称")
    private String title;

    @ApiModelProperty(value = "视频截图")
    private String poster;

    @ApiModelProperty(value = "医生")
    private String doctor;

    @ApiModelProperty(value = "患者")
    private String patient;

    @ApiModelProperty(value = "手术简介")
    private String operationInfo;

}
