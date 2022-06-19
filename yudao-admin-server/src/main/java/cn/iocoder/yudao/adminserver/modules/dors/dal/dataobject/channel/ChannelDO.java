package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel;

import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamDirectionType;
import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamProtocol;
import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 通道 DO
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
     * 所属房间
     */
    private Integer room;
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
     * 流协议（SRT/RTSP）
     */
    private StreamProtocol streamProtocol;
    /**
     * 流类型（编码器输出流/网络输入流)
     *
     * 枚举 {@link StreamDirectionType}
     */
    private StreamDirectionType streamType;
    /**
     * 通道名称
     */
    private String name;
    /**
     * 通道图标
     */
    private String icon;
    /**
     * 流地址
     */
    private String url;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 频道JSON数据信息
     */
    private String jsonInfo;
    /**
     * 是否显示
     */
    private Boolean display;
    /**
     * 连接至矩阵的端口
     */
    private String matrixPort;
    /**
     * 串口
     */
    private String serialPort;
    /**
     * 是否绑定摄像机
     */
    private Boolean isCamera;
    /**
     * 摄像机云台控制串口信息
     */
    private String cameraSerialPort;
    /**
     * 备注
     */
    private String remarks;


}
