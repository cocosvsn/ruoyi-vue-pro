package cn.iocoder.yudao.framework.file.core.handler;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * 文件 处理器接口
 * @author bunco
 */
public interface FileHandler extends AutoCloseable {

    /**
     * 文件上传
     *
     * @param relativePath 上传文件存储的相对路径（带文件名）
     * @param contentType  上传文件类型
     * @param in           上传文件流
     * @throws IOException IO异常
     */
    void upload(String relativePath, String contentType, InputStream in) throws IOException;

    /**
     * 文件下载
     *
     * @param relativePath 下载文件的相对路径
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream download(String relativePath) throws IOException;

    /**
     * 删除文件
     *
     * @param relativePath 文件的相对路径
     * @throws IOException IO异常
     */
    void delete(String relativePath) throws IOException;

    /**
     * 文件可以访问的url路径
     *
     * @param relativePath 文件的相对路径
     * @return 文件访问Url
     */
    String getUrl(String relativePath);

    String getUrlPrefix();

    default String getId(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        }
        int start = relativePath.lastIndexOf("/");
        if (-1 == start) {
            throw new IllegalArgumentException("相对路径错误");
        }
        int end = relativePath.lastIndexOf(".");
        if (-1 == end || end <= start) {
            throw new IllegalArgumentException("相对路径错误");
        }
        return relativePath.substring(start + 1, end);
    }

    default boolean isImage(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        return contentType.startsWith("image/");
    }

    /**
     * 图片压缩
     *
     * @param source       will be auto close
     * @param imageQuality 输出质量
     * @param scale        缩放
     * @return 输入流
     */
    default InputStream imageCompress(InputStream source, float imageQuality, double scale) throws IOException {
        try (InputStream in = source; ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Thumbnails.of(in).outputQuality(imageQuality).scale(scale).toOutputStream(bos);
            return new ByteArrayInputStream(bos.toByteArray());
        }
    }
}