package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 手术视频 Excel VO
 *
 * @author Bunco
 */
@Data
public class OperationVideoExcelVO {

    @ExcelProperty("主键（自增）")
    private Integer id;

    @ExcelProperty("所属手术室")
    private Integer room;

    @ExcelProperty("手术名称")
    private String title;

    @ExcelProperty("医生")
    private String doctor;

    @ExcelProperty("患者")
    private String patient;

    @ExcelProperty("手术简介")
    private String operationInfo;

    @ExcelProperty("创建时间")
    private Date createTime;

}
