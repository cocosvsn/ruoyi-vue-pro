package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 频道 DO
 *
 * @author Bunco
 */
@TableName("dors_channel")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDO extends BaseDO {

    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 所属设备
     */
    private Integer device;
    /**
     * 频道ID
     */
    private Integer channelId;
    /**
     * 频道类型（vi/usb/net/ndi/file/mix)
     *
     * 枚举 {@link TODO dors_channel_type 对应的类}
     */
    private String type;
    /**
     * 频道名称
     */
    private String name;
    /**
     * 频道JSON数据信息
     */
    private String jsonInfo;
    /**
     * 是否显示
     */
    private Boolean display;
    /**
     * 备注
     */
    private String remarks;

}
