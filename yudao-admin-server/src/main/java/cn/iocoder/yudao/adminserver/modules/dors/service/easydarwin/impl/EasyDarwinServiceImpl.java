package cn.iocoder.yudao.adminserver.modules.dors.service.easydarwin.impl;

import cn.iocoder.yudao.adminserver.modules.dors.service.easydarwin.EasyDarwinService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service
public class EasyDarwinServiceImpl implements EasyDarwinService {

    @Value("${yudao.dors.darwin.api.url:http://ed:10008/api/v1/}")
    private String darwinApiUrl;
    @Resource
    private RestTemplate restTemplate;

    public EasyDarwinServiceImpl() {
        log.error("EasyDarwinServiceImpl();");
    }

    /**
     * 启动RTSP拉转推流
     * @param sourceStream RTSP流源地址
     * @param destPath 转推流的路径
     * @return 返回拉流的ID
     */
    public String startPushStream(String sourceStream, String destPath) {
        String url = darwinApiUrl + "stream/start?url={1}&transType=UDP&customPath={2}";
//        Map<String, String> params = new HashMap<>();
//        params.put("url", sourceStream);
//        params.put("customPath", destPath);
//        // 拉流传输模式， TCP / UDP
//        params.put("transType", "UDP");
        try {
            log.info("startPushStream sourceStream: {} destPath: {}", sourceStream, destPath);
            String id = this.restTemplate.getForObject(url, String.class, sourceStream, destPath);
            log.info("{} result: {}", url, id);
            return StringUtils.substringBetween(id, "\"");
        } catch (Exception e) {
            log.error("启动推拉流出错：", e);
            return null;
        }
    }

    /**
     * 停止RTSP拉转推流
     * @param id 拉流的ID
     * @return OK 表示成功，其它表示失败
     */
    public String stopPushStream(String id) {
        String url = darwinApiUrl + "stream/stop?id="+id;
//        Map<String, String> params = new HashMap<>();
//        params.put("id", id);
        try {
            log.info("{}", url);
            String result = this.restTemplate.getForObject(url, String.class);
            log.info("{} result: {}", url, result);
            return result;
        } catch (Exception e) {
            log.error("停止推拉流出错：", e);
            return null;
        }

    }

    /**
     * 根据推流路径停止推流
     * @param path 推流路径
     * @return 拉流id
     */
    public void stopPushStreamByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        String url = darwinApiUrl + "pushers";
        log.info("{}", url);
        String result = this.restTemplate.getForObject(url, String.class);
        log.info("{} result: {}", url, result);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(result);
            JsonNode rowsNode = node.get("rows");
            rowsNode.forEach(data -> {
                if(path.equals(data.get("path").asText())) {
                    String id = data.get("id").asText();
                    String stopUrl = darwinApiUrl + "stream/stop?id="+id;
                    log.info("stop stream url: {}", stopUrl);
                    String stopResult = this.restTemplate.getForObject(stopUrl, String.class);
                    log.info("{} result: {}", stopUrl, stopResult);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("路径解析失败：", e);
        }
    }
}
