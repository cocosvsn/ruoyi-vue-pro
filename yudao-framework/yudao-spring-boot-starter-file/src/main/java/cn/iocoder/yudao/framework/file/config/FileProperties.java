package cn.iocoder.yudao.framework.file.config;

import cn.iocoder.yudao.framework.file.core.enums.StoreType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件 配置类
 *
 * @author Bunco
 */
@ConfigurationProperties("yudao.file")
@Data
public class FileProperties {
    private StoreType storeType = StoreType.NONE;
    /**
     * 可访问的网址前缀，如https://xxx.com
     */
    private String urlPrefix;
    // 图片压缩开关
    private boolean enableImageCompress = false;
    // 图片输出质量 1 百分百输出 < 1
    private float imageQuality = 1;
    // 缩放1 为不缩放 < 1
    private double iamgeScale = 1;

    private LocalProperties local = new LocalProperties();
    private AliyunOssProperties aliyun = new AliyunOssProperties();
    private MinioProperties minio = new MinioProperties();

    @Getter
    @Setter
    public static class LocalProperties {
        /**
         * 存储目录，一般为可以处理静态容器的路径，如nginx,tomcat 下可以通过url访问的目录,如果/data/files
         *
         * @local.upload.directory@ for local test
         */
        private String directory;
    }

    @Getter
    @Setter
    public static class AliyunOssProperties {
        /**
         * 阿里云oss 存储空间
         * <a>https://help.aliyun.com/document_detail/177682.html?spm=a2c4g.11186623.6.634.4a102c4cZPZ0M5</a>
         */
        private String bucketName;
        /**
         * 对外可访问的域名,线上推荐用内网，开发测试如果无法连接则使用公网
         * <a>https://help.aliyun.com/document_detail/31837.html?spm=a2c4g.11186623.6.625.3e4933e1OCqmWA</a>
         */
        private String endpoint;
        /**
         * 分配的ID
         */
        private String accessKeyId;
        /**
         * 分配密钥
         */
        private String accessKeySecret;
    }

    @Getter
    @Setter
    public static class MinioProperties {
        /**
         * Minio 存储桶名
         */
        private String bucketName;
        /**
         * 对外可访问的域名,线上推荐用内网，开发测试如果无法连接则使用公网
         */
        private String endpoint;
        /**
         * 分配的用户
         */
        private String accessKey;
        /**
         * 分配密钥
         */
        private String secretKey;
    }
}
