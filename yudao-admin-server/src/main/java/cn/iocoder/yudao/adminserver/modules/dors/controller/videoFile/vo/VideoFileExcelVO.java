package cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 手术视频文件 Excel VO
 *
 * @author Bunco
 */
@Data
public class VideoFileExcelVO {

    @ExcelProperty("主键（自增）")
    private Integer id;

    @ExcelProperty("关联手术")
    private Integer operationVideo;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("文件类型")
    private String contentType;

    @ExcelProperty("文件大小")
    private Integer fileSize;

    @ExcelProperty("相对路径")
    private String relativePath;

    @ExcelProperty("创建时间")
    private Date createTime;

}
