package cn.iocoder.yudao.framework.udpserver.config;

import cn.iocoder.yudao.framework.udpserver.core.service.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * UdpServer 配置类
 *
 * @author bunco
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UdpProperties.class)
@ConditionalOnProperty(prefix = "yudao.udp.server", value = "enable")
public class YudaoUdpServerAutoConfiguration {
    // 转换通道
    private static final String TRANSFORMER_CHANNEL = "transformer";
    // 消息处理通道
    private static final String HANDLER_CHANNEL = "handler";

    @Resource
    private UdpProperties udpProperties;
    @Autowired
    private MessageProcessor messageProcessor;

    @Bean
    public UnicastReceivingChannelAdapter getUnicastReceivingChannelAdapter() {
        //实例化一个udp 监听指定端口
        UnicastReceivingChannelAdapter adapter = new  UnicastReceivingChannelAdapter(udpProperties.getPort());
        adapter.setOutputChannelName(TRANSFORMER_CHANNEL);
        return adapter;
    }

    @Transformer(inputChannel = TRANSFORMER_CHANNEL,
            outputChannel = HANDLER_CHANNEL)
    public String transformer(@Payload byte[] payload, @Headers Map<String, Object> headers) throws UnsupportedEncodingException {
        log.debug("transformer({})", payload);
        return new String(payload, udpProperties.getMessageChartset());//把接收的数据转化为字符串
    }

    // 需要路由的话，加上下面这段
//    @Router(inputChannel="routing")
//    public String routing(String message) {
//        log.debug("routing({})", message);
//        return udpProperties.getChannelHandlerName();
//    }

    @ServiceActivator(inputChannel = HANDLER_CHANNEL)
    public void udpMessageHandle(String message, @Headers Map<String, Object> headers) {
        log.debug("udpMessageHandle", message);
        // 需要进行异步处理
        this.messageProcessor.handlerMessage(message, headers);
    }
}
