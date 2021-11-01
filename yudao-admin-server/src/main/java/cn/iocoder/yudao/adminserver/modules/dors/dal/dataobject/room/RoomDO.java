package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 房间 DO
 *
 * @author Bunco
 */
@TableName("dors_room")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDO extends BaseDO {

    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 类型（手术室/会议室）
     *
     * 枚举 {@link RoomType}
     */
    private RoomType type;
    /**
     * 房间名称
     */
    private String name;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 绑定的设备列表
     */
    @TableField(exist = false)
    private List<DeviceDO> devices;
}
