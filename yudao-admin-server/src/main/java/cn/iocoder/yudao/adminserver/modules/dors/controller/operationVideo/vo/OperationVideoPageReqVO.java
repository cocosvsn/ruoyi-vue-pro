package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel("手术视频分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperationVideoPageReqVO extends PageParam {

    @ApiModelProperty(value = "所属手术室")
    private Integer room;

    @ApiModelProperty(value = "手术名称")
    private String title;

    @ApiModelProperty(value = "医生")
    private String doctor;

    @ApiModelProperty(value = "患者")
    private String patient;

    @ApiModelProperty(value = "手术简介")
    private String operationInfo;

    @ApiModelProperty(value = "上线状态")
    private Boolean onlineStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始创建时间")
    private Date beginCreateTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;

}
