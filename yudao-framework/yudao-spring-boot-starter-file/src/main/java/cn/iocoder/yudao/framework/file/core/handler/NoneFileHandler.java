package cn.iocoder.yudao.framework.file.core.handler;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传空实现，让{@code @Autowire} 默认注入文件处理类不报错
 *
 * @author bunco
 */
public class NoneFileHandler implements FileHandler {

    @Override
    public void upload(String relativePath, String contentType, InputStream in) throws IOException {
        throw new RuntimeException("please config FileHandler properties");
    }

    @Override
    public InputStream download(String relativePath) throws IOException {
        throw new RuntimeException("please config FileHandler properties");
    }

    @Override
    public void delete(String relativePath) throws IOException {
        throw new RuntimeException("please config FileHandler properties");
    }

    @Override
    public String getUrl(String relativePath) {
        return relativePath;
    }

    @Override
    public String getUrlPrefix() {
        throw new RuntimeException("please config FileHandler properties");
    }

    @Override
    public void close() throws Exception {

    }
}
