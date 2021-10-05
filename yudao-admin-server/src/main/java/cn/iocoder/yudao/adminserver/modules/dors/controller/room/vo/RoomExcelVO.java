package cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;


/**
 * 房间 Excel VO
 *
 * @author Bunco
 */
@Data
public class RoomExcelVO {

    @ExcelProperty("主键（自增）")
    private Integer id;

    @ExcelProperty(value = "类型（手术室/会议室）", converter = DictConvert.class)
    @DictFormat("dors_room_type") // TODO 代码优化：建议设置到对应的 XXXDictTypeConstants 枚举类中
    private String type;

    @ExcelProperty("房间名称")
    private String name;

    @ExcelProperty("创建时间")
    private Date createTime;

    @ExcelProperty("备注")
    private String remarks;

}
