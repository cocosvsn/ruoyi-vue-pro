package cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel(value = "手术视频文件 Excel 导出 Request VO", description = "参数和 VideoFilePageReqVO 是一致的")
@Data
public class VideoFileExportReqVO {

    @ApiModelProperty(value = "关联手术")
    private Integer operationVideo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "文件类型")
    private String contentType;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "开始创建时间")
    private Date beginCreateTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;

}
