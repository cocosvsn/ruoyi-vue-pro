package cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;


/**
 * 频道 Excel VO
 *
 * @author Bunco
 */
@Data
public class ChannelExcelVO {

    @ExcelProperty("主键（自增）")
    private Integer id;

    @ExcelProperty("所属设备")
    private Integer device;

    @ExcelProperty("频道ID")
    private Integer channelId;

    @ExcelProperty(value = "频道类型（vi/usb/net/ndi/file/mix)", converter = DictConvert.class)
    @DictFormat("dors_channel_type") // TODO 代码优化：建议设置到对应的 XXXDictTypeConstants 枚举类中
    private String type;

    @ExcelProperty("频道名称")
    private String name;

    @ExcelProperty("频道JSON数据信息")
    private String jsonInfo;

    @ExcelProperty("创建时间")
    private Date createTime;

}
