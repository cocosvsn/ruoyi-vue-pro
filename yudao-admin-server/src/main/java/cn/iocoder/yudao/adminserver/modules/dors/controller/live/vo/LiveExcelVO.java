package cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 直播 Excel VO
 *
 * @author bunco
 */
@Data
public class LiveExcelVO {

    @ExcelProperty("直播流名称")
    private String name;

    @ExcelProperty("封面图")
    private String icon;

    @ExcelProperty("地址")
    private String addr;

    @ExcelProperty("创建时间")
    private Date createTime;

}
