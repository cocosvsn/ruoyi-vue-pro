package cn.iocoder.yudao.adminserver.framework.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    // 配置 RestTemplate
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate template = new RestTemplate(factory);
        template.getMessageConverters().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringHttpMessageConverter = (StringHttpMessageConverter) converter;
                stringHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        return template;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        // 创建一个 httpCilent 简单工厂
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 设置连接超时
        factory.setConnectTimeout(15000);
        // 设置读取超时
        factory.setReadTimeout(5000);
        return factory;
    }
}
