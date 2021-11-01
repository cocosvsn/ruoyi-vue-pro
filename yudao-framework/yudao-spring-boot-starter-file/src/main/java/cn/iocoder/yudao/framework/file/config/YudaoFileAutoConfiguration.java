package cn.iocoder.yudao.framework.file.config;

import cn.iocoder.yudao.framework.file.core.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * 文件 配置类
 *
 * @author bunco
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class YudaoFileAutoConfiguration {

    @Resource
    private FileProperties fileProperties;

    public YudaoFileAutoConfiguration() {
        log.info("YudaoFileAutoConfiguration()");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "yudao.file.store-type", havingValue = "none")
    public FileHandler noneFileHandler() {
        log.info("current configuration file store type is {}", fileProperties.getStoreType());
        return new NoneFileHandler();
    }

    @Bean
    @ConditionalOnProperty(name = "yudao.file.store-type", havingValue = "local")
    public FileHandler localFileHandler() {
        log.info("current configuration file store type is {}", fileProperties.getStoreType());
        return new LocalFileHandler(fileProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "yudao.file.store-type", havingValue = "aliyun_oss")
    public FileHandler aliyunOssFileHandler() {
        log.info("current configuration file store type is {}", fileProperties.getStoreType());
        return new AliyunOssHandler(fileProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "yudao.file.store-type", havingValue = "minio")
    public FileHandler minioFileHandler() {
        log.info("current configuration file store type is {}", fileProperties.getStoreType());
        return new MinioHandler(fileProperties);
    }
}
