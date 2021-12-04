package cn.iocoder.yudao.adminserver.modules.dors.controller.ws;

import cn.iocoder.yudao.adminserver.framework.util.SpringCtxUtils;
import cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message.MessageInfo;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message.MessageInfo.*;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType.MEETING_ROOM;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType.OPERATING_ROOM;

@Slf4j
@Controller
@ServerEndpoint("/ws/dors/{roomId}")
public class WebSocketServerEndpoint {

    // 在线人数
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Session> clientMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, RoomDO> roomMap = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private RoomService roomService = SpringCtxUtils.getBean(RoomService.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Integer roomId) throws IOException {
        log.info("[onOpen][房间编号({}) 接入]", roomId);
        RoomDO room = this.roomService.get(roomId);
        if (null == room) {
            log.warn("The room not found by room id: {}, will close the session.", roomId);
            session.close();
        }
        // 向其它客户端广播用户上线的消息。
        clientMap.forEach((id, s) -> sendMessage(s, buildOnlineMessage(room, roomMap.get(id))));
        // 缓存数据
        roomMap.put(roomId, room);
        if(clientMap.containsKey(roomId)) {
            // 客户端会话列表中移除当前会话
            clientMap.remove(roomId);
            // 重新将当前会话加入客户端会话列表中
            clientMap.put(roomId, session);
            // 当前在线房间数量保持不变
            log.info("用户连接: {}, 当前在线房间数为: {}", roomId, onlineCount.get());
        } else {
            // 将当前会话加入到客户端会话列表中
            clientMap.put(roomId, session);
            // 在线数量加1
            log.info("新用户连接: {}, 当前在线房间数为: {}", roomId, onlineCount.incrementAndGet());
        }
        // 如果当前上线的是会议室，则向会议室下发已经在线的手术室列表。
        if (MEETING_ROOM.equals(room.getType())) {
            sendAllOnlineOperatingRoom(session);
        }
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("roomId") Integer roomId, String message) throws JsonProcessingException {
        log.info("[onMessage][session({}：{}) 接收到一条消息({})]", session, roomId, message); // 生产环境下，请设置成 debug 级别
        try {
            MessageInfo messageInfo = this.objectMapper.readValue(message, MessageInfo.class);
            log.debug("{}", messageInfo);
            switch (messageInfo.getTypeOf()) {
                case dialing:                // 呼叫
                case dialing_busy:           // 呼叫占线
                case cancel_dialing:         // 取消呼叫
                case answer:                 // 接听
                case reject:                 // 拒接
                case ring_off:               // 挂断
                case control_serial_port:    // 串口控制
                case change_serial_port:     // 切换串口
                    // 转发消息至目标
                    sendMessage(clientMap.get(messageInfo.getTo().getId()), message);
                    break;
                default:
                    log.warn("未知类型消息：{}", message);
                    break;
            }
        } catch (Exception e) {
            log.error("解析消息失败：", e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") Integer roomId, CloseReason closeReason) {
        log.info("[onClose][房间编号({}) 连接关闭。关闭原因是({})}]", roomId, closeReason);
        if(clientMap.containsKey(roomId)) {
            // 客户端会话列表中移除当前会话
            clientMap.remove(roomId);
            // 在线数量减1
            log.info("用户关闭连接: {}, 当前在线房间数为: {}", roomId, onlineCount.decrementAndGet());
        } else {
            // 当前会话，不存在于客户端会话列表中，存在异常，需要跟踪排错，为何会出现该现象。
            log.warn("用户关闭连接: {}, 当前房间不存在于客户端会话列表中，当前在线房间数为: {}， 客户端会话列表数量为：{}",
                    roomId, onlineCount.get(), clientMap.size());
        }

        // 向其它客户端广播用户下线的消息。
        clientMap.entrySet().stream().filter(s -> s.getKey() != roomId)
                .forEach(s -> sendMessage(s.getValue(), buildOfflineMessage(roomMap.get(roomId), roomMap.get(s.getKey()))));
//        clientMap.forEach((id, s) -> s.getAsyncRemote().sendObject(buildOfflineMessage(roomMap.get(roomId), roomMap.get(id))));

        // 在线房间列表，必须广播完才能移除。
        roomMap.remove(roomId);
    }

    @OnError
    public void onError(Session session, @PathParam("roomId") Integer roomId, Throwable throwable) {
        log.error("[onError][房间编号({}) 发生异常]", roomId, throwable);
    }

    /**
     * 发送消息
     * @param session
     * @param message
     */
    private void sendMessage(Session session, String message) {
        log.info("发送消息： {}", message);
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 发送消息
     * @param session
     * @param message
     */
    private void sendMessage(Session session, MessageInfo message) {
        log.info("发送消息： {}", message);
        try {
            sendMessage(session, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("JSON 处理异常：", e);
        }
    }

    /**
     * 向客户端发送所有在线会议室列表
     * @param session 客户端会话
     */
    private void sendAllOnlineOperatingRoom(Session session) {
        List<RoomDO> onlineOperatingRoom = roomMap.entrySet().stream()
                .filter(s -> OPERATING_ROOM.equals(s.getValue().getType()))
                .map(s -> s.getValue())
                .collect(Collectors.toList());
        // 当前在线手术室列表不为空，才下发。
        log.debug("sendAllOnlineOperatingRoom({}): {}", session, onlineOperatingRoom);
        if (!onlineOperatingRoom.isEmpty()) {
            sendMessage(session, buildOnlineOperatingRoomsMessage(onlineOperatingRoom));
        }
    }
}
