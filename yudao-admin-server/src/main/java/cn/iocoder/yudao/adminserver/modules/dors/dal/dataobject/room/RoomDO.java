package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.EncoderType;
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
     * 录制状态
     */
    private Boolean record;
    /**
     * 正在录制状态的视频ID
     */
    private Integer recordVideo;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 频道列表（用于手术室显示）
     */
    @TableField(exist = false)
    private List<ChannelDO> channels;

    /**
     * 编码器设备列表
     */
    @TableField(exist = false)
    private List<DeviceDO> encoderDevices;

    /**
     * 解码器设备列表
     */
    @TableField(exist = false)
    private List<DeviceDO> decoderDevices;

    /**
     * IPC设备列表
     */
    @TableField(exist = false)
    private List<DeviceDO> ipcDevices;

    /**
     * TV设备列表
     */
    @TableField(exist = false)
    private List<DeviceDO> tvDevices;
}
