package cn.iocoder.yudao.adminserver.modules.dors.enums;

/**
 * 消息类型
 * @author bunco
 */
public enum MessageType {
    /**
     * 心跳消息
     */
    heartbeat,
    /**
     * 上线消息
     */
    online,
    /**
     * 下线消息
     */
    offline,
    /**
     * 呼叫
     */
    dialing,
    /**
     * 取消呼叫
     */
    cancel_dialing,
    /**
     * 呼叫占线
     */
    dialing_busy,
    /**
     * 接听
     */
    answer,
    /**
     * 拒接
     */
    reject,
    /**
     * 挂断
     */
    ring_off,
    /**
     * 在线手机室列表
     */
    online_operating_rooms,
    /**
     * 在线会议室列表
     */
    online_meeting_rooms,
    /**
     * 串口控制
     */
    control_serial_port,
    /**
     * 切换并打开串口
     */
    change_serial_port;
}
