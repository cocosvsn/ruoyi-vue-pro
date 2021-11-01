package cn.iocoder.yudao.framework.file.core.handler;

import cn.iocoder.yudao.framework.file.config.FileProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bunco
 */
public class AliyunOssHandler implements FileHandler {

    private FileProperties fileProperties;
    private OSS ossClient;

    public AliyunOssHandler(FileProperties fileProperties) {
        Assert.hasText(fileProperties.getAliyun().getBucketName(),
                "FileProperties.AliyunOssProperties bucketName must not be empty");
        Assert.hasText(fileProperties.getAliyun().getEndpoint(),
                "FileProperties.AliyunOssProperties endpoint must not be empty");
        Assert.hasText(fileProperties.getAliyun().getAccessKeyId(),
                "FileProperties.AliyunOssProperties accessKeyId must not be empty");
        Assert.hasText(fileProperties.getAliyun().getAccessKeySecret(),
                "FileProperties.AliyunOssProperties accessKeySecret must not be empty");
        Assert.hasText(fileProperties.getUrlPrefix(), "FileProperties urlPrefix must not be empty");
        this.fileProperties = fileProperties;
        ossClient = new OSSClientBuilder().build(fileProperties.getAliyun().getEndpoint(),
            fileProperties.getAliyun().getAccessKeyId(), fileProperties.getAliyun().getAccessKeySecret());
    }

    @Override
    public void upload(String relativePath, String contentType, InputStream in) throws IOException {
        if (isImage(contentType) && fileProperties.isEnableImageCompress()) {
            InputStream compressedInputStream =
                imageCompress(in, fileProperties.getImageQuality(), fileProperties.getIamgeScale());
            ossClient.putObject(fileProperties.getAliyun().getBucketName(), handleRelativePath(relativePath),
                compressedInputStream);
        } else {
            ossClient.putObject(fileProperties.getAliyun().getBucketName(), handleRelativePath(relativePath), in);
        }
        if (null != in) {
            in.close();
        }
    }

    @Override
    public InputStream download(String relativePath) throws IOException {
        OSSObject object =
            ossClient.getObject(fileProperties.getAliyun().getBucketName(), this.handleRelativePath(relativePath));
        return object.getObjectContent();
    }

    @Override
    public void delete(String relativePath) throws IOException {
        ossClient.deleteObject(fileProperties.getAliyun().getBucketName(), this.handleRelativePath(relativePath));
    }

    @Override
    public String getUrl(String relativePath) {
        return StringUtils.isNotBlank(relativePath) ? fileProperties.getUrlPrefix() + relativePath : null;
    }

    @Override
    public void close() throws Exception {
        if (null != ossClient) {
            ossClient.shutdown();
        }
    }

    /**
     * 阿里云路径不能以/ 开始
     *
     * @param relativePath
     * @return
     */
    private String handleRelativePath(String relativePath) {
        Assert.hasLength(relativePath, "relativePath is empty");
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    @Override
    public String getUrlPrefix() {
        return fileProperties.getUrlPrefix();
    }
}
