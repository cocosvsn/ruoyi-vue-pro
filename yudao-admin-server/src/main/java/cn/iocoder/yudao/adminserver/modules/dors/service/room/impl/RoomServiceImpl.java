package cn.iocoder.yudao.adminserver.modules.dors.service.room.impl;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.channel.ChannelMapper;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.device.DeviceMapper;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.operationVideo.OperationVideoMapper;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.videoFile.VideoFileMapper;
import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import cn.iocoder.yudao.adminserver.modules.dors.enums.RoomType;
import cn.iocoder.yudao.adminserver.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.yudao.adminserver.modules.infra.dal.mysql.config.InfConfigMapper;
import cn.iocoder.yudao.framework.file.config.FileProperties;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.room.RoomConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.room.RoomMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.RoomService;
import org.springframework.web.client.RestTemplate;

import static cn.iocoder.yudao.adminserver.modules.infra.enums.InfDictTypeConstants.DEFAULT_OPERATION_VIDEO_ONLINE_STATUS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 房间 Service 实现类
 *
 * @author Bunco
 */
@Service
@Validated
public class RoomServiceImpl implements RoomService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ChannelMapper channelMapper;
    @Resource
    private VideoFileMapper videoFileMapper;
    @Resource
    private OperationVideoMapper operationVideoMapper;
    @Resource
    private InfConfigMapper infConfigMapper;
    @Resource
    private FileProperties fileProperties;
    @Resource
    private RestTemplate restTemplate;
    @Value("${yudao.dors.record.api.url:http://192.168.1.248:7011/api/record/}")
    private String dorsRecordApiUrl;

    @Override
    public Integer create(RoomCreateReqVO createReqVO) {
        // 插入
        RoomDO roomDO = RoomConvert.INSTANCE.convert(createReqVO);
        roomMapper.insert(roomDO);
        // 返回
        return roomDO.getId();
    }

    @Override
    public void update(RoomUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateExists(updateReqVO.getId());
        // 更新
        RoomDO updateObj = RoomConvert.INSTANCE.convert(updateReqVO);
        roomMapper.updateById(updateObj);
    }

    /**
     * 房间绑定设备
     *
     * @param bindDeviceReqVO 设备绑定信息
     */
    @Override
    public void bindDevice(@Valid RoomBindDeviceReqVO bindDeviceReqVO) {
        DeviceDO padDevice = this.deviceMapper.selectById(bindDeviceReqVO.getPadId());
        if (null == padDevice) {
            throw exception(DEVICE_NOT_EXISTS);
        }
        DeviceDO encoderDevice = this.deviceMapper.selectById(bindDeviceReqVO.getEncoderId());
        if (null == encoderDevice) {
            throw exception(DEVICE_NOT_EXISTS);
        }
        padDevice.setRoom(bindDeviceReqVO.getRoomId());
        this.deviceMapper.updateById(padDevice);
        encoderDevice.setRoom(bindDeviceReqVO.getRoomId());
        this.deviceMapper.updateById(encoderDevice);
    }

    @Override
    public void delete(Integer id) {
        // 校验存在
        this.validateExists(id);
        // 删除
        roomMapper.deleteById(id);
        // 将绑定该房间的设备解绑
        this.deviceMapper.selectList(new QueryWrapperX<DeviceDO>().eqIfPresent("room", id))
                .forEach(deviceDO -> {this.deviceMapper.updateById(deviceDO.setRoom(null));});
    }

    private void validateExists(Integer id) {
        if (roomMapper.selectById(id) == null) {
            throw exception(ROOM_NOT_EXISTS);
        }
    }

    @Override
    public RoomDO get(Integer id) {
        return roomMapper.selectById(id);
    }

    @Override
    public List<RoomDO> getList(Collection<Integer> ids) {
        return roomMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<RoomDO> getPage(RoomPageReqVO pageReqVO) {
        PageResult<RoomDO> pageResult = roomMapper.selectPage(pageReqVO);
        // 补全设备信息
        pageResult.getList().forEach(roomDO -> { roomDO.setDevices(this.deviceMapper.selectList(
                new QueryWrapperX<DeviceDO>().eqIfPresent("room", roomDO.getId())));});
        return pageResult;
    }

    @Override
    public List<RoomDO> getList(RoomExportReqVO exportReqVO) {
        return roomMapper.selectList(exportReqVO);
    }

    /**
     * 根据设备MAC地址查询房间及房间绑定的设备信息
     *
     * @param mac 设备MAC地址
     * @return 房间及房间绑定的设备信息
     */
    public RoomDO getByMac(String mac) {
//        return this.roomMapper.selectByDeviceMac(mac);
        DeviceDO deviceDO = this.deviceMapper.selectOne("device_mac", mac);
        RoomDO roomDO = this.roomMapper.selectById(deviceDO.getRoom());
        roomDO.setDevices(this.deviceMapper.selectList(new QueryWrapperX<DeviceDO>().eq("room", roomDO.getId())));
        return roomDO;
    }

    /**
     * 获得手术室房间列表
     * @return 房间列表
     */
    public List<RoomDO> getOperatingRoomList() {
        return this.roomMapper.selectList(new QueryWrapperX<RoomDO>()
                .eq("type", RoomType.OPERATING_ROOM)
        );
    }

    /**
     * 启动指定房间视频通道列表录制
     * @param id 房间编号
     * @param channelIds 通道编号列表
     * @return
     */
    @SneakyThrows
    public boolean startRecord(Integer id, List<Integer> channelIds) {
        logger.info("updateRecord thread[{}]: {}, {}", Thread.currentThread().getName(), id, channelIds);
        RoomDO roomDO = this.roomMapper.selectById(id);
        if (roomDO.getRecord()) {
            throw new IllegalArgumentException(roomDO.getName() + "已经在录制，请勿重复操作！");
        }

        List<ChannelDO> channelDOList = this.channelMapper.selectBatchIds(channelIds);
        roomDO.setDevices(this.deviceMapper.selectList(new QueryWrapperX<DeviceDO>().eq("room", roomDO.getId())));

        DeviceDO deviceDO = getEncoder(roomDO.getDevices());
        InfConfigDO infConfigDO = this.infConfigMapper.selectByKey(DEFAULT_OPERATION_VIDEO_ONLINE_STATUS);

        OperationVideoDO operationVideoDO = OperationVideoDO.builder()
                .room(roomDO.getId())
                .title("未命名")
                .onlineStatus(Boolean.valueOf(infConfigDO.getValue()))
                .build();
        int ret = this.operationVideoMapper.insert(operationVideoDO);
        logger.info("Save OperationVideoDO: {}, result: {}", operationVideoDO, ret);

        String poster = null;
        for (ChannelDO channel: channelDOList) {
            // 提交每个通道的录制。
            String relativePath = roomDO.getName() + File.separator + DateUtil.today() + File.separator +
                    channel.getName() + "-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String input = "rtsp://"+ deviceDO.getLastOnlineIp()+ "/stream" + channel.getChannelId();
            String output = fileProperties.getLocal().getDirectory() + relativePath + ".mp4";
            File outputFile = new File(output);
            if (!outputFile.getParentFile().exists()) {
                logger.info("Record output dir【{}】 not exist, make it: {}", output, outputFile.getParentFile().mkdirs());
            }

            /**
             * 截图
             * ffmpeg -i rtsp://192.168.1.230/stream0 -y -f mjpeg -t 0.001 -s 1920x1080 test.jpg
             * ffmpeg -i rtsp://192.168.1.230/stream0 -y -f mjpeg -ss 0 -t 0.001  -s 1920x1080 test.jpg
             * ffmpeg -i rtsp://192.168.1.230/stream0 -y -f image2 -ss 1.0 -t 0.001  -s 1920x1080 test.jpg
             */
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[] { "/usr/bin/ffmpeg", "-i", input, "-y", "-f", "mjpeg", "-t", "0.001",  "-s", "1920x1080",
                    fileProperties.getLocal().getDirectory() + relativePath + ".jpg"});
            process.waitFor();

            logger.info("截图完成，开始请求！");
            // 请求接口录制视频。
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", input);
            jsonObject.put("video_type", "MP4");
            jsonObject.put("video_path", output);
            Result result = request("start", jsonObject);

            logger.info("截图完成，开始请求！");
            // 保存录制记录
            VideoFileDO videoFileDO = VideoFileDO.builder()
                    .operationVideo(operationVideoDO.getId())
                    .title(channel.getName() + " - " + channel.getChannelId())
                    .relativePath(relativePath + ".mp4")
//                    .contentType(result.getData())
                    .taskId(result.getData())
                    .build();
            this.videoFileMapper.insert(videoFileDO);

            if (null == poster) {
                // 将第一个通道的截图，赋值给海报。
                poster = relativePath + ".jpg";
            }
        }

        // 更新海报信息。
        operationVideoDO.setPoster(poster);
        this.operationVideoMapper.updateById(operationVideoDO);

        roomDO.setRecord(true);
        roomDO.setRecordVideo(operationVideoDO.getId());
        this.roomMapper.updateById(roomDO);
        return true;
    }

    /**
     * 停止指定房间视频录制
     * @param id 房间编号
     * @return
     */
    @SneakyThrows
    public void stopRecord(Integer id) {
        RoomDO roomDO = this.roomMapper.selectById(id);
        if (roomDO.getRecord()) { // 只有正在录制的状态，才需要停止。
            OperationVideoDO operationVideoDO = this.operationVideoMapper.selectById(roomDO.getRecordVideo());
            List<VideoFileDO> videoFileDOS = this.videoFileMapper.selectList("operation_video", roomDO.getRecordVideo());

            long totalSize = 0;
            for (VideoFileDO vf: videoFileDOS) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("task_id", vf.getTaskId());
                Result result = request("stop", jsonObject);
                logger.info("stop record room id: {}, room title: {}, task id: {}, video file id: {}, result: {}",
                        id, roomDO.getName(), vf.getTaskId(), vf.getId(), result.isSuccess());

                // 停止录制后，获取实际录制的视频文件大小。
                // Tip： 当前由于偷懒没有判断停止录制的状态，因此可能有Bug存在。
                File file = new File(fileProperties.getLocal().getDirectory() + vf.getRelativePath());
                if (file.exists()) {
                    totalSize += file.length();
                    vf.setFileSize(file.length());
                }
                // 更新通道视频文件大小至数据库。
                this.videoFileMapper.updateById(vf);
            }

            // 更新全部通道视频文件总大小至数据库。
            operationVideoDO.setTotalSize(totalSize);
            // 根据配置项目，设置录制视频的默认上线状态。
            InfConfigDO infConfigDO = this.infConfigMapper.selectByKey(DEFAULT_OPERATION_VIDEO_ONLINE_STATUS);
            operationVideoDO.setOnlineStatus("1".equals(infConfigDO.getValue()) ? true : false);
            this.operationVideoMapper.updateById(operationVideoDO);

            // 更新房间录制状态至数据库。
            roomDO.setRecord(false);
            roomDO.setRecordVideo(null);
            this.roomMapper.updateById(roomDO);
        } else {
            logger.warn("{} record status is false!", roomDO.getName());
        }
    }

    private DeviceDO getEncoder(List<DeviceDO> list) {
        if (null == list) {
            return null;
        }
        for (DeviceDO d: list) {
            if (DeviceType.C3531D.equals(d.getType())) {
                return d;
            }
        }
        return null;
    }

    @SneakyThrows
    private Result request(String method, JSONObject json) {
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置请求类型
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 封装参数和头信息
        HttpEntity<JSONObject> httpEntity = new HttpEntity(json, httpHeaders);
        String url = dorsRecordApiUrl + method;
        logger.info("request url: {}, {}", url, json.toJSONString());
        ResponseEntity<String> mapResponseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String result = mapResponseEntity.getBody();
        logger.info("response: {}", result);
        return new ObjectMapper().readValue(result, Result.class);
    }

    @Data
    static class Result implements Serializable {
        private boolean success;
        private String data;
        private String msg;
    }
}
