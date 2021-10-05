package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device;

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
     * 设备类型（PAD/ENCODER/DECODER）
     *
     * 枚举 {@link DeviceType}
     */
    private DeviceType type;
    /**
     * 设备序列号
     */
    private String serialNo;
    /**
     * 设备MAC地址
     */
    private String deviceMac;
    /**
     * 应用程序版本
     */
    private String appVersion;
    /**
     * SDK版本
     */
    private String sdkVersion;
    /**
     * 系统版本
     */
    private String sysVersion;
    /**
     * 最近一次上线IP地址
     */
    private String lastOnlineIp;
    /**
     * 最近一次上线时间
     */
    private Date lastOnlineTime;
    /**
     * 备注
     */
    private String remarks;

}
