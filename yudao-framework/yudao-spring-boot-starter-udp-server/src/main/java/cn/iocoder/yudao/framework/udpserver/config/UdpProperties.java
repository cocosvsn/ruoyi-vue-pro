package cn.iocoder.yudao.framework.udpserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Udp Server 配置类
 *
 * @author Bunco
 */
@ConfigurationProperties("yudao.udp.server")
@Data
public class UdpProperties {
    /**
     * 是否启用Udp服务
     */
    private boolean enable = false;
    /**
     * UDP 服务监听端口
     */
    private Integer port = 5432;
    /**
     * 消息字符编码
     */
    private String messageChartset = "UTF-8";
}
