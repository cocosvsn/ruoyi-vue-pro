package cn.iocoder.yudao.adminserver.modules.dors.controller.ws;

import cn.iocoder.yudao.adminserver.framework.util.SpringCtxUtils;
import cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message.MessageInfo;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType;
import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamDirectionType;
import cn.iocoder.yudao.adminserver.modules.dors.service.easydarwin.EasyDarwinService;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.RoomService;
import cn.iocoder.yudao.adminserver.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.yudao.adminserver.modules.infra.service.config.InfConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message.MessageInfo.*;
import static cn.iocoder.yudao.adminserver.modules.dors.controller.ws.message.MessageInfo.buildHeartbeatMessage;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType.MEETING_ROOM;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType.OPERATING_ROOM;
import static cn.iocoder.yudao.adminserver.modules.infra.enums.InfDictTypeConstants.SERVER_IP;

@Slf4j
@Controller
@ServerEndpoint("/ws/dors/{roomId}")
public class WebSocketServerEndpoint {

    // 在线人数
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Session> clientMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, RoomDO> roomMap = new ConcurrentHashMap<>();
    /**
     * 拉流列表。
     * {
     *     手术室ID： {
     *         会议室ID_PUSH：[会议室拉流列表]
     *         会议室ID_PUSH：[会议室推流列表]
     *     }
     * }
     */
    private static final String KEY_FORMAT_PULL = "{0}_PULL";
    private static final String KEY_FORMAT_PUSH = "{0}_PUSH";
    private static ConcurrentHashMap<Integer, Map<String, List<String>>> streamList = new ConcurrentHashMap<>();
    // 用于记录手术室被占用的拉流通道数量。
    private static ConcurrentHashMap<Integer, Integer> roomUsedInputChannelSize = new ConcurrentHashMap<>();
    // 用于记录拉转推映射关系。
    private static ConcurrentHashMap<Integer, String> channelStreamMapping = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private RoomService roomService = SpringCtxUtils.getBean(RoomService.class);
    private InfConfigService infConfigService = SpringCtxUtils.getBean(InfConfigService.class);
    private EasyDarwinService easyDarwinService = SpringCtxUtils.getBean(EasyDarwinService.class);

    /**
     * 每10秒钟检测一次Session有效性
     */
    @Scheduled(cron="*/10 * * * * ?")
    private void checkSession() {
        for (Map.Entry<Integer, Session> sessionEntry : clientMap.entrySet()) {
            try {
                sendMessage(sessionEntry.getValue(), buildHeartbeatMessage());
            } catch (Exception e) {
                log.error("发送告警消息失败,删除session"+sessionEntry.getValue());
                try {
                    clientMap.remove(sessionEntry.getKey());
                } catch (Exception e1){
                    log.error("根据sessionId删除失败，清除sessionMap");
                    clientMap.clear();
                }
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Integer roomId) throws IOException {
        log.info("[onOpen][房间编号({}) 接入]", roomId);
        RoomDO room = this.roomService.getRoom(roomId);
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
                case reject:                 // 拒接
                case control_serial_port:    // 串口控制
                case change_serial_port:     // 切换串口
                    // 转发消息至目标
                    sendMessage(clientMap.get(messageInfo.getTo().getId()), message);
                    break;
                case answer:                 // 接听
                    // 启动推拉流
                    startPushStream(messageInfo);
                    // 应答时，需要根据映射关系调整流地址，便于客户端修改各窗口拉流。
                    List<Map<String, String>> channels = (List<Map<String, String>>) messageInfo.getData();
                    for (Map<String, String> kv: channels) {
                        kv.put("url", channelStreamMapping.get(kv.get("id")));
                    }
                    // 转发消息至目标
                    sendMessage(clientMap.get(messageInfo.getTo().getId()), objectMapper.writeValueAsString(messageInfo));
                    break;
                case ring_off:               // 挂断
                    // 停止推拉流
                    stopPushStream(messageInfo);
                    // 转发消息至目标
                    sendMessage(clientMap.get(messageInfo.getTo().getId()), message);
                    break;
                default:
                    log.warn("其它类型消息：{}", message);
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
    public void onError(Session session, @PathParam("roomId") Integer roomId, Throwable throwable) throws IOException {
        log.error("[onError][房间编号({}) 发生异常]", roomId, throwable);
        session.close();
    }

    /**
     * 发送消息
     * @param session
     * @param message
     */
    private void sendMessage(Session session, String message) throws IOException {
        log.info("发送消息： {}", message);
        synchronized (session) {
            session.getBasicRemote().sendText(message);
        }
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
        } catch (IOException e) {
            log.error("发送消息异常：", e);
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

    /**
     * 手术室连线，开始推流
     */
    private void startPushStream(MessageInfo messageInfo) {
        // 应答一定是手术室向会议室，因此from为手术室，to为会议室
        Integer fromId = messageInfo.getFrom().getId();
        Integer toId = messageInfo.getTo().getId();
        RoomDO fromRoom = this.roomService.get(fromId);
        RoomDO toRoom = this.roomService.get(toId);
        // 根据类型区分通道拉流与推流
        List<ChannelDO> fromRoomOutputChannels = fromRoom.getChannels();
        List<ChannelDO> fromRoomInputChannels = fromRoom.getDecoderDevices().stream().flatMap(
                s -> s.getChannels().stream()).collect(Collectors.toList());
        List<ChannelDO> toRoomOutputChannels = toRoom.getChannels();
        List<ChannelDO> toRoomInputChannels = toRoom.getDecoderDevices().stream().flatMap(
                s -> s.getChannels().stream()).collect(Collectors.toList());
        InfConfigDO serverIpConfig = infConfigService.getConfigByKey(SERVER_IP);
        // 示教室拉流列表
        List<String> pullStreamList = new ArrayList<>();
        // 示教室推流列表
        List<String> pushStreamList = new ArrayList<>();
        Map<String, List<String>> meetingRoomPullStreams = new HashMap<>();
        /**
         * 最小匹配通道数量
         * 示教室拉手术室的流。
         *  假设：手术室4路输出，示教室只有3路输入，则只能匹配到三路流
         * 手术室拉示教室的流。
         *  假设：示教室1路输出，手术室有4路输入，则一次连接占用一路程，最多允许4间手术室连接
         */
        int meetingRoomChannelSize = Math.min(fromRoomOutputChannels.size(), toRoomInputChannels.size());
        for (int i = 0; i < meetingRoomChannelSize; i ++) {
            String id = this.easyDarwinService.startPushStream(fromRoomOutputChannels.get(i).getUrl(),
                    StringUtils.substringAfter(toRoomInputChannels.get(i).getUrl(), serverIpConfig.getValue()));
            pullStreamList.add(id);
            // 保存拉转推映射关系。
            channelStreamMapping.put(fromRoomOutputChannels.get(i).getId(), toRoomInputChannels.get(i).getUrl());
        }
        // 获取当前房间已使用的输入通道数量，每当有一个示教室连线成功后，将增加示教室输出通道数量的占用数。
        Integer operatingRoomUsedChannelSize = roomUsedInputChannelSize.get(fromId);
        if (null == operatingRoomUsedChannelSize) {
            operatingRoomUsedChannelSize = 0;
        }
        int operatingRoomChannelSize = Math.min(toRoomOutputChannels.size(),
                fromRoomInputChannels.size() - operatingRoomUsedChannelSize);
        for (int i = 0; i < operatingRoomChannelSize; i ++) {
            String id = this.easyDarwinService.startPushStream(toRoomOutputChannels.get(i).getUrl(),
                    StringUtils.substringAfter(fromRoomInputChannels.get(operatingRoomUsedChannelSize + i).getUrl(),
                            serverIpConfig.getValue()));
            pushStreamList.add(id);
            // 保存拉转推映射关系。
            channelStreamMapping.put(toRoomOutputChannels.get(i).getId(),
                    fromRoomInputChannels.get(operatingRoomUsedChannelSize + i).getUrl());
        }
        operatingRoomUsedChannelSize += operatingRoomChannelSize;
        meetingRoomPullStreams.put(MessageFormat.format(KEY_FORMAT_PULL, toId), pullStreamList);
        meetingRoomPullStreams.put(MessageFormat.format(KEY_FORMAT_PUSH, toId), pushStreamList);
        roomUsedInputChannelSize.put(fromId, operatingRoomUsedChannelSize);
        streamList.put(fromId, meetingRoomPullStreams);
    }

    /**
     * 断开连线，停止推流
     */
    private void stopPushStream(MessageInfo messageInfo) {
        RoomDO fromRoom = messageInfo.getFrom();
        RoomDO toRoom = messageInfo.getTo();
        // 手术室发起断开
        if (RoomType.OPERATING_ROOM.equals(fromRoom.getType())) {
            // 停止全部推流。
            streamList.get(fromRoom.getId()).values().forEach(
                    idList -> idList.forEach(id -> easyDarwinService.stopPushStream(id)));
            roomUsedInputChannelSize.put(fromRoom.getId(), 0);
        } else {
            // 停止某一个示教室推流。
            Integer operatingRoomId = toRoom.getId();
            Integer meetingRoomId = fromRoom.getId();
            Map<String, List<String>> meetingRoomStreams = streamList.get(operatingRoomId);
            String pullStreamListKey = MessageFormat.format(KEY_FORMAT_PULL, meetingRoomId);
            String pushStreamListKey = MessageFormat.format(KEY_FORMAT_PUSH, meetingRoomId);
            List<String> meetingRoomPushStreamList = meetingRoomStreams.get(pushStreamListKey);
            // 停止示教室拉流
            meetingRoomStreams.get(pullStreamListKey).forEach(id -> easyDarwinService.stopPushStream(id));
            meetingRoomStreams.get(pullStreamListKey).clear(); // 清空。
            // 停止示教室推流
            meetingRoomPushStreamList.forEach(id -> easyDarwinService.stopPushStream(id));
            meetingRoomPushStreamList.clear(); // 清空。
            // 恢复手术室可用通道数量
            roomUsedInputChannelSize.put(operatingRoomId,
                    roomUsedInputChannelSize.get(operatingRoomId) - meetingRoomPushStreamList.size());
        }
    }
}
