package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 设备 DO
 *
 * @author Bunco
 */
@TableName("dors_device")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDO extends BaseDO {
    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 所属房间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer room;
    /**
     * 设备类型（ENCODER/DECODER/IPC/TV）
     *
     * 枚举 {@link DeviceType}
     */
    private DeviceType type;
    /**
     * 设备生产商
     */
    private String manufacturer;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备IP地址
     */
    private String ip;
    /**
     * 设备通道数量
     */
    private Integer channelCount;
    /**
     * 连接至矩阵的端口
     */
    private String matrixPort;
    /**
     * 设备序列号
     */
    @TableField(exist = false)
    private String serialNo;
    /**
     * 设备MAC地址
     */
    private String mac;
    /**
     * 设备登陆密码
     */
    private String loginPass;
    /**
     * 应用程序版本
     */
    @TableField(exist = false)
    private String appVersion;
    /**
     * SDK版本
     */
    @TableField(exist = false)
    private String sdkVersion;
    /**
     * 系统版本
     */
    @TableField(exist = false)
    private String sysVersion;
    /**
     * 最近一次上线IP地址
     */
    @TableField(exist = false)
    private String lastOnlineIp;
    /**
     * 最近一次上线时间
     */
    @TableField(exist = false)
    private Date lastOnlineTime;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 设备下绑定的通道列表
     */
    @TableField(exist = false)
    private List<ChannelDO> channels;
}
