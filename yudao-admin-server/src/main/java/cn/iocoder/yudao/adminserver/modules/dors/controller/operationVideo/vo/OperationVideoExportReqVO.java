package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel(value = "手术视频 Excel 导出 Request VO", description = "参数和 OperationVideoPageReqVO 是一致的")
@Data
public class OperationVideoExportReqVO {

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

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始创建时间")
    private Date beginCreateTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;

}
