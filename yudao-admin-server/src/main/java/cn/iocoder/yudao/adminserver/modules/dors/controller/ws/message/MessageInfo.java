package cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.MessageType;
import lombok.*;

import java.util.List;

/**
 * 聊天消息结构
 * @author bunco
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class MessageInfo<T> {
    private MessageType typeOf;
    private RoomDO from;
    private RoomDO to;
    private T data;

    /**
     * 构建房间上线消息
     * @param from 上线房间
     * @param to 消息广播房间
     * @return
     */
    public static MessageInfo buildOnlineMessage(RoomDO from, RoomDO to) {
        return buildMessage(MessageType.online, from, to, null);
    }

    /**
     * 构建房间下线消息
     * @param from 下线房间
     * @param to 消息广播房间
     * @return
     */
    public static MessageInfo buildOfflineMessage(RoomDO from, RoomDO to) {
        return buildMessage(MessageType.offline, from, to, null);
    }

    /**
     * 构建获取在线手术室列表消息
     * @param data 在线手术室列表
     * @return
     */
    public static MessageInfo buildOnlineOperatingRoomsMessage(List<RoomDO> data) {
        return buildMessage(MessageType.online_operating_rooms, null, null, data);
    }

    /**
     * 构建心跳消息
     * @return
     */
    public static MessageInfo buildHeartbeatMessage() {
        return buildMessage(MessageType.heartbeat, null, null, null);
    }

    private static <T> MessageInfo buildMessage(MessageType typeOf, RoomDO from, RoomDO to, T data) {
        return MessageInfo.builder()
                .typeOf(typeOf)
                .from(from)
                .to(to)
                .data(data)
                .build();
    }
}
