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
import cn.iocoder.yudao.framework.file.config.FileProperties;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.room.RoomConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.room.RoomMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.RoomService;

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
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private FileProperties fileProperties;
    private Map<Integer, List<RecordVideoRunnable>> recordList = new HashMap<>();

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
    public boolean startRecord(Integer id, List<Integer> channelIds) {
        logger.info("updateRecord thread[{}]: {}, {}", Thread.currentThread().getName(), id, channelIds);
        RoomDO roomDO = this.roomMapper.selectById(id);
        if (roomDO.getRecord()) {
            throw new IllegalArgumentException(roomDO.getName() + "已经在录制，请勿重复操作！");
        }

        List<ChannelDO> channelDOList = this.channelMapper.selectBatchIds(channelIds);
        roomDO.setDevices(this.deviceMapper.selectList(new QueryWrapperX<DeviceDO>().eq("room", roomDO.getId())));

        List<RecordVideoRunnable> recordChannelList = new ArrayList<>();
        DeviceDO deviceDO = getEncoder(roomDO.getDevices());

        OperationVideoDO operationVideoDO = OperationVideoDO.builder()
                .room(roomDO.getId())
                .title("未命名")
                .build();
        int ret = this.operationVideoMapper.insert(operationVideoDO);
        logger.info("Save OperationVideoDO: {}, result: {}", operationVideoDO, ret);

        for (ChannelDO channel: channelDOList) {
            // 提交每个通道的录制。
            String relativePath = roomDO.getName() + File.separator + DateUtil.today() + File.separator +
                    channel.getName() + "-" + DateUtil.format(new Date(), "yyyyMMddHHmmss") +
                    ".mp4";
            VideoFileDO videoFileDO = VideoFileDO.builder()
                    .operationVideo(operationVideoDO.getId())
                    .title(channel.getName() + " - " + channel.getChannelId())
                    .relativePath(relativePath)
                    .build();
            this.videoFileMapper.insert(videoFileDO);
            String input = "rtsp://"+ deviceDO.getLastOnlineIp()+ "/stream" + channel.getChannelId();
            String output = fileProperties.getLocal().getDirectory() + relativePath;
            File outputFile = new File(output);
            if (!outputFile.getParentFile().exists()) {
                logger.info("Record output dir not exist, make it: {}", outputFile.getParentFile().mkdirs());
            }
            RecordVideoRunnable r = new RecordVideoRunnable(input, output);
            this.threadPoolTaskExecutor.execute(r);
            recordChannelList.add(r);
        }
        recordList.put(id, recordChannelList);

        roomDO.setRecord(true);
        this.roomMapper.updateById(roomDO);
        return true;
    }

    /**
     * 停止指定房间视频录制
     * @param id 房间编号
     * @return
     */
    public void stopRecord(Integer id) {
        List<RecordVideoRunnable> list = recordList.get(id);
        list.forEach(s -> {
            try {
                if (s.isRunning()) {
                    Runtime.getRuntime().exec(new String[]{"kill", "-9", String.valueOf(s.getPid())}).waitFor();
                }
            } catch (IOException e) {
                logger.error("", e);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        });
        RoomDO roomDO = this.roomMapper.selectById(id);
        roomDO.setRecord(false);
        this.roomMapper.updateById(roomDO);
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

    class RecordVideoRunnable implements Runnable {
        private String inputStream;
        private String outputPath;
        private Process process;
        private boolean running;

        public RecordVideoRunnable(String inputStream, String outputPath) {
            this.inputStream = inputStream;
            this.outputPath = outputPath;
        }

        public Long getPid() {
            if (null != process) {
                return process.pid();
            }
            return null;
        }

        public boolean isRunning() {
            return running;
        }

        @SneakyThrows
        @Override
        public void run() {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(new String[]{"/usr/bin/ffmpeg", "-i", inputStream, "-c", "copy", "-f", "mp4", outputPath});
            running = true;
            int exitValue = process.waitFor();
            logger.info("Record process exit: {}", exitValue);
        }
    }
}
