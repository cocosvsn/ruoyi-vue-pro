package cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 应用版本 Excel VO
 *
 * @author Bunco
 */
@Data
public class VersionExcelVO {

    @ExcelProperty("编号（自增）")
    private Integer id;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("包名")
    private String packageName;

    @ExcelProperty("版本号")
    private Integer versionNum;

    @ExcelProperty("版本代码")
    private String versionCode;

    @ExcelProperty("是否强制升级")
    private Boolean forceUpdate;

    @ExcelProperty("主入口Activity")
    private String mainActivity;

    @ExcelProperty("开发者")
    private String author;

    @ExcelProperty("开发者联系方式")
    private String authorContact;

    @ExcelProperty("描述")
    private String description;

    @ExcelProperty("浏览次数")
    private Integer hits;

    @ExcelProperty("下载次数")
    private Integer dlCount;

    @ExcelProperty("更新时间")
    private Date updateTime;

}
