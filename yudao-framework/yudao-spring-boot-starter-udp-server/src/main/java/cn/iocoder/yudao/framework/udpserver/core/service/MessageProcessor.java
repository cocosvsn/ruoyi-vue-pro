package cn.iocoder.yudao.framework.udpserver.core.service;

import java.util.Map;

/**
 * UDP 报文消息处理服务
 */
public interface MessageProcessor {

    /**
     * 消息处理器
     * @param message
     */
    void handlerMessage(String message, Map<String, Object> headers);
}
