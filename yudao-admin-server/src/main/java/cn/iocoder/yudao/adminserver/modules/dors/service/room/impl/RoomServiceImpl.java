package cn.iocoder.yudao.adminserver.modules.dors.service.room.impl;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.ChannelCreateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.ChannelUpdateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceCreateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.DeviceUpdateReqVO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.channel.ChannelConvert;
import cn.iocoder.yudao.adminserver.modules.dors.convert.device.DeviceConvert;
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
import cn.iocoder.yudao.adminserver.modules.dors.enums.StreamDirectionType;
import cn.iocoder.yudao.adminserver.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.yudao.adminserver.modules.infra.dal.mysql.config.InfConfigMapper;
import cn.iocoder.yudao.framework.file.config.FileProperties;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
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

        // 保存操控面板设备
        DeviceDO padDeviceDO = DeviceConvert.INSTANCE.convert(createReqVO.getPad());
        padDeviceDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
        padDeviceDO.setType(DeviceType.PAD);
        this.deviceMapper.insert(padDeviceDO);

        // 保存编码器设备
        for (DeviceCreateReqVO encoderReqVO: createReqVO.getEncoderDevices()) {
            DeviceDO encoderDeviceDO = DeviceConvert.INSTANCE.convert(encoderReqVO);
            encoderDeviceDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
            encoderDeviceDO.setType(DeviceType.ENCODER);
            this.deviceMapper.insert(encoderDeviceDO);

            // 保存关联的通道信息
            for (ChannelCreateReqVO encoderChannelReqVO: encoderReqVO.getChannels()) {
                ChannelDO encoderChannelDO = ChannelConvert.INSTANCE.convert(encoderChannelReqVO);
                encoderChannelDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
                encoderChannelDO.setDevice(encoderDeviceDO.getId());
                encoderChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                this.channelMapper.insert(encoderChannelDO);
            }
        }

        // 保存解码器设备
        for (DeviceCreateReqVO decoderReqVO: createReqVO.getDecoderDevices()) {
            DeviceDO decoderDeviceDO = DeviceConvert.INSTANCE.convert(decoderReqVO);
            decoderDeviceDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
            decoderDeviceDO.setType(DeviceType.DECODER);
            this.deviceMapper.insert(decoderDeviceDO);

            // 保存关联的通道信息
            for (ChannelCreateReqVO decoderChannelReqVO: decoderReqVO.getChannels()) {
                ChannelDO decoderChannelDO = ChannelConvert.INSTANCE.convert(decoderChannelReqVO);
                decoderChannelDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
                decoderChannelDO.setDevice(decoderDeviceDO.getId());
                decoderChannelDO.setStreamType(StreamDirectionType.NETWORK_INPUT);
                this.channelMapper.insert(decoderChannelDO);
            }
        }

        // 保存IPC设备
        for (DeviceCreateReqVO ipcReqVO: createReqVO.getIpcDevices()) {
            DeviceDO ipcDeviceDO = DeviceConvert.INSTANCE.convert(ipcReqVO);
            ipcDeviceDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
            ipcDeviceDO.setType(DeviceType.IPC);
            this.deviceMapper.insert(ipcDeviceDO);

            // 保存关联的通道信息
            for (ChannelCreateReqVO ipcChannelReqVO: ipcReqVO.getChannels()) {
                ChannelDO ipcChannelDO = ChannelConvert.INSTANCE.convert(ipcChannelReqVO);
                ipcChannelDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
                ipcChannelDO.setDevice(ipcDeviceDO.getId());
                ipcChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                this.channelMapper.insert(ipcChannelDO);
            }
        }

        // 保存大屏设备
        for (DeviceCreateReqVO tvReqVO: createReqVO.getTvDevices()) {
            DeviceDO tvDeviceDO = DeviceConvert.INSTANCE.convert(tvReqVO);
            tvDeviceDO.setRoom(roomDO.getId()); // 保存之前设置关联关系
            tvDeviceDO.setType(DeviceType.TV);
            this.deviceMapper.insert(tvDeviceDO);

            // 大屏设备没有通道关系不需要绑定
        }

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

        // 查询出来当前房间的所有设备和通道
        List<DeviceDO> deviceDOList = this.deviceMapper.selectList(
                new QueryWrapperX<DeviceDO>().eq("room", updateReqVO.getId()));
        List<ChannelDO> channelDOList = this.channelMapper.selectList(
                new QueryWrapperX<ChannelDO>().eq("room", updateReqVO.getId()));

        // 更新操控面板信息
        DeviceDO padDeviceDO = DeviceConvert.INSTANCE.convert(updateReqVO.getPad());
        this.deviceMapper.updateById(padDeviceDO);

        // 编码器设备
        List<DeviceDO> encoderDevices = new ArrayList<>();
        // 解码器设备
        List<DeviceDO> decoderDevices = new ArrayList<>();
        // IPC设备
        List<DeviceDO> ipcDevices = new ArrayList<>();
        // 大屏设备
        List<DeviceDO> tvDevices = new ArrayList<>();
        // 将设备分类
        for (DeviceDO deviceDO: deviceDOList) {
            // 将通道分类归置于设备中
            List<ChannelDO> deviceChannels = new ArrayList<>();
            for (ChannelDO channelDO: channelDOList) {
                if (deviceDO.getId().equals(channelDO.getDevice())) {
                    deviceChannels.add(channelDO);
                }
            }
            deviceDO.setChannels(deviceChannels);
            switch (deviceDO.getType()) {
                case ENCODER:
                    encoderDevices.add(deviceDO);
                    break;
                case DECODER:
                    decoderDevices.add(deviceDO);
                    break;
                case IPC:
                    ipcDevices.add(deviceDO);
                    break;
                case TV:
                    tvDevices.add(deviceDO);
                    break;
                default:
                    break;
            }
        }

        // 找出更新的设备列表中不存在的编码器设备，将其删除。
        encoderDevices.stream().filter(existDevice -> !updateReqVO.getEncoderDevices().stream().filter(
               updateDevice -> existDevice.getId().equals(updateDevice.getId())
        ).findAny().isPresent()).forEach(persistDevice -> {
            // 删除设备下的全部通道
            this.channelMapper.delete(new QueryWrapperX<ChannelDO>()
                    .eq("room", updateReqVO.getId())
                    .eq("device", persistDevice.getId()));
            // 删除设备
            this.deviceMapper.deleteById(persistDevice.getId());
        });

        // 找出更新的设备列表中不存在的解码器设备，将其删除。
        decoderDevices.stream().filter(existDevice -> !updateReqVO.getDecoderDevices().stream().filter(
                updateDevice -> existDevice.getId().equals(updateDevice.getId())
        ).findAny().isPresent()).forEach(persistDevice -> {
            // 删除设备下的全部通道
            this.channelMapper.delete(new QueryWrapperX<ChannelDO>()
                    .eq("room", updateReqVO.getId())
                    .eq("device", persistDevice.getId()));
            // 删除设备
            this.deviceMapper.deleteById(persistDevice.getId());
        });
        // 找出更新的设备列表中不存在的IPC设备，将其删除。
        ipcDevices.stream().filter(existDevice -> !updateReqVO.getIpcDevices().stream().filter(
                updateDevice -> existDevice.getId().equals(updateDevice.getId())
        ).findAny().isPresent()).forEach(persistDevice -> {
            // 删除设备下的全部通道
            this.channelMapper.delete(new QueryWrapperX<ChannelDO>()
                    .eq("room", updateReqVO.getId())
                    .eq("device", persistDevice.getId()));
            // 删除设备
            this.deviceMapper.deleteById(persistDevice.getId());
        });
        // 找出更新的设备列表中不存在的大屏设备，将其删除。
        tvDevices.stream().filter(existDevice -> !updateReqVO.getTvDevices().stream().filter(
                updateDevice -> existDevice.getId().equals(updateDevice.getId())
        ).findAny().isPresent()).forEach(persistDevice -> {
            // 删除设备下的全部通道
            this.channelMapper.delete(new QueryWrapperX<ChannelDO>()
                    .eq("room", updateReqVO.getId())
                    .eq("device", persistDevice.getId()));
            // 删除设备
            this.deviceMapper.deleteById(persistDevice.getId());
        });

        // 找出更新的编码器设备列表中不存在的通道，将其删除。
        encoderDevices.stream().flatMap(existDevice -> existDevice.getChannels().stream())
                .filter(existChannel -> !updateReqVO.getEncoderDevices().stream().flatMap(
                        updateDevice -> updateDevice.getChannels().stream()).filter(
                                updateChannel -> existChannel.getId().equals(updateChannel.getId()))
                .findAny().isPresent()).forEach(persistChannel -> {
                    this.channelMapper.deleteById(persistChannel.getId());
        });
        // 找出更新的解码器设备列表中不存在的通道，将其删除。
        decoderDevices.stream().flatMap(existDevice -> existDevice.getChannels().stream())
                .filter(existChannel -> !updateReqVO.getDecoderDevices().stream().flatMap(
                        updateDevice -> updateDevice.getChannels().stream()).filter(
                        updateChannel -> existChannel.getId().equals(updateChannel.getId()))
                        .findAny().isPresent()).forEach(persistChannel -> {
            this.channelMapper.deleteById(persistChannel.getId());
        });
        // 找出更新的IPC设备列表中不存在的通道，将其删除。
        ipcDevices.stream().flatMap(existDevice -> existDevice.getChannels().stream())
                .filter(existChannel -> !updateReqVO.getIpcDevices().stream().flatMap(
                        updateDevice -> updateDevice.getChannels().stream()).filter(
                        updateChannel -> existChannel.getId().equals(updateChannel.getId()))
                        .findAny().isPresent()).forEach(persistChannel -> {
            this.channelMapper.deleteById(persistChannel.getId());
        });
        // 找出更新的大屏设备列表中不存在的通道，将其删除。
        tvDevices.stream().flatMap(existDevice -> existDevice.getChannels().stream())
                .filter(existChannel -> !updateReqVO.getTvDevices().stream().flatMap(
                        updateDevice -> updateDevice.getChannels().stream()).filter(
                        updateChannel -> existChannel.getId().equals(updateChannel.getId()))
                        .findAny().isPresent()).forEach(persistChannel -> {
            this.channelMapper.deleteById(persistChannel.getId());
        });

        // 遍历编码器设备列表
        for (DeviceUpdateReqVO encoderDeviceUpdateReqVO: updateReqVO.getEncoderDevices()) {
            DeviceDO encoderDeviceDO = DeviceConvert.INSTANCE.convert(encoderDeviceUpdateReqVO);
            if (null == encoderDeviceDO.getId()
                    || 0 >= encoderDeviceDO.getId()) { // 新增设备保存
                encoderDeviceDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                encoderDeviceDO.setType(DeviceType.ENCODER);
                this.deviceMapper.insert(encoderDeviceDO);

                // 保存关联的通道信息
                for (ChannelUpdateReqVO encoderChannelUpdateReqVO: encoderDeviceUpdateReqVO.getChannels()) {
                    ChannelDO encoderChannelDO = ChannelConvert.INSTANCE.convert(encoderChannelUpdateReqVO);
                    encoderChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                    encoderChannelDO.setDevice(encoderDeviceDO.getId());
                    encoderChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                    this.channelMapper.insert(encoderChannelDO);
                }
            } else { // 更新设备保存
                this.deviceMapper.updateById(encoderDeviceDO);

                // 更新关联的通道信息
                for (ChannelUpdateReqVO encoderChannelUpdateReqVO: encoderDeviceUpdateReqVO.getChannels()) {
                    ChannelDO encoderChannelDO = ChannelConvert.INSTANCE.convert(encoderChannelUpdateReqVO);
                    if (null == encoderChannelDO.getId() || 0 >= encoderChannelDO.getId()) {
                        // 新增通道保存
                        encoderChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                        encoderChannelDO.setDevice(encoderDeviceDO.getId());
                        encoderChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                        this.channelMapper.insert(encoderChannelDO);
                    } else { // 更新通道保存
                        this.channelMapper.updateById(encoderChannelDO);
                    }
                }
            }
        }

        // 遍历解码器设备列表
        for (DeviceUpdateReqVO decoderDeviceUpdateReqVO: updateReqVO.getDecoderDevices()) {
            DeviceDO decoderDeviceDO = DeviceConvert.INSTANCE.convert(decoderDeviceUpdateReqVO);
            if (null == decoderDeviceDO.getId()
                    || 0 >= decoderDeviceDO.getId()) { // 新增设备保存
                decoderDeviceDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                decoderDeviceDO.setType(DeviceType.DECODER);
                this.deviceMapper.insert(decoderDeviceDO);

                // 保存关联的通道信息
                for (ChannelUpdateReqVO decoderChannelUpdateReqVO: decoderDeviceUpdateReqVO.getChannels()) {
                    ChannelDO decoderChannelDO = ChannelConvert.INSTANCE.convert(decoderChannelUpdateReqVO);
                    decoderChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                    decoderChannelDO.setDevice(decoderDeviceDO.getId());
                    decoderChannelDO.setStreamType(StreamDirectionType.NETWORK_INPUT);
                    this.channelMapper.insert(decoderChannelDO);
                }
            } else { // 更新设备保存
                this.deviceMapper.updateById(decoderDeviceDO);

                // 更新关联的通道信息
                for (ChannelUpdateReqVO decoderChannelUpdateReqVO: decoderDeviceUpdateReqVO.getChannels()) {
                    ChannelDO decoderChannelDO = ChannelConvert.INSTANCE.convert(decoderChannelUpdateReqVO);
                    if (null == decoderChannelDO.getId() || 0 >= decoderChannelDO.getId()) {
                        // 新增通道保存
                        decoderChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                        decoderChannelDO.setDevice(decoderDeviceDO.getId());
                        decoderChannelDO.setStreamType(StreamDirectionType.NETWORK_INPUT);
                        this.channelMapper.insert(decoderChannelDO);
                    } else { // 更新通道保存
                        this.channelMapper.updateById(decoderChannelDO);
                    }
                }
            }
        }

        // 遍历IPC设备列表
        for (DeviceUpdateReqVO ipcDeviceUpdateReqVO: updateReqVO.getIpcDevices()) {
            DeviceDO ipcDeviceDO = DeviceConvert.INSTANCE.convert(ipcDeviceUpdateReqVO);
            if (null == ipcDeviceDO.getId()
                    || 0 >= ipcDeviceDO.getId()) { // 新增设备保存
                ipcDeviceDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                ipcDeviceDO.setType(DeviceType.IPC);
                this.deviceMapper.insert(ipcDeviceDO);

                // 保存关联的通道信息
                for (ChannelUpdateReqVO ipcChannelUpdateReqVO: ipcDeviceUpdateReqVO.getChannels()) {
                    ChannelDO ipcChannelDO = ChannelConvert.INSTANCE.convert(ipcChannelUpdateReqVO);
                    ipcChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                    ipcChannelDO.setDevice(ipcDeviceDO.getId());
                    ipcChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                    this.channelMapper.insert(ipcChannelDO);
                }
            } else { // 更新设备保存
                this.deviceMapper.updateById(ipcDeviceDO);

                // 更新关联的通道信息
                for (ChannelUpdateReqVO ipcChannelUpdateReqVO: ipcDeviceUpdateReqVO.getChannels()) {
                    ChannelDO ipcChannelDO = ChannelConvert.INSTANCE.convert(ipcChannelUpdateReqVO);
                    if (null == ipcChannelDO.getId() || 0 >= ipcChannelDO.getId()) {
                        // 新增通道保存
                        ipcChannelDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                        ipcChannelDO.setDevice(ipcDeviceDO.getId());
                        ipcChannelDO.setStreamType(StreamDirectionType.ENCODE_OUTPUT);
                        this.channelMapper.insert(ipcChannelDO);
                    } else { // 更新通道保存
                        this.channelMapper.updateById(ipcChannelDO);
                    }
                }
            }
        }

        // 遍历大屏设备列表
        for (DeviceUpdateReqVO tvDeviceUpdateReqVO: updateReqVO.getTvDevices()) {
            DeviceDO tvDeviceDO = DeviceConvert.INSTANCE.convert(tvDeviceUpdateReqVO);
            if (null == tvDeviceDO.getId() || 0 >= tvDeviceDO.getId()) { // 新增设备保存
                tvDeviceDO.setRoom(updateReqVO.getId()); // 保存之前设置关联关系
                tvDeviceDO.setType(DeviceType.TV);
                this.deviceMapper.insert(tvDeviceDO);
            } else { // 更新设备保存
                this.deviceMapper.updateById(tvDeviceDO);
            }
            // 大屏设备没有通道，因此不需要更新。
        }
    }

    @Override
    public void delete(Integer id) {
        // 校验存在
        this.validateExists(id);
        // 删除房间绑定的通道
        this.channelMapper.delete(new QueryWrapperX<ChannelDO>().eq("room", id));
        // 删除房间绑定的设备
        this.deviceMapper.delete(new QueryWrapperX<DeviceDO>().eq("room", id));
        // 删除房间
        this.roomMapper.deleteById(id);
    }

    private void validateExists(Integer id) {
        if (roomMapper.selectById(id) == null) {
            throw exception(ROOM_NOT_EXISTS);
        }
    }

    @Override
    public RoomDO get(Integer id) {
        RoomDO roomDO = roomMapper.selectById(id);
        // 补充房间的设备与通道信息
        completeDeviceAndChannel(roomDO);
        return roomDO;
    }

    /**
     * 获取房间信息
     * @param id 房间编号
     * @return
     */
    public RoomDO getRoom(Integer id) {
        return roomMapper.selectById(id);
    }

    @Override
    public List<RoomDO> getList(Collection<Integer> ids) {
        List<RoomDO> list = roomMapper.selectBatchIds(ids);
        list.forEach(room -> completeDeviceAndChannel(room));
        return list;
    }

    @Override
    public PageResult<RoomDO> getPage(RoomPageReqVO pageReqVO) {
        PageResult<RoomDO> pageResult = roomMapper.selectPage(pageReqVO);
        // 补充房间的设备与通道信息
        pageResult.getList().forEach(roomDO -> completeDeviceAndChannel(roomDO));
        return pageResult;
    }

    @Override
    public List<RoomDO> getList(RoomExportReqVO exportReqVO) {
        return roomMapper.selectList(exportReqVO);
    }

    /**
     * 补充房间的设备与通道信息
     * @param roomDO
     */
    private void completeDeviceAndChannel(RoomDO roomDO) {
        List<DeviceDO> deviceDOList = this.deviceMapper.selectList(
                new QueryWrapperX<DeviceDO>().eq("room", roomDO.getId()));
        List<ChannelDO> channelDOList = this.channelMapper.selectList(
                new QueryWrapperX<ChannelDO>().eq("room", roomDO.getId()).orderByAsc("sort"));

        if (null == roomDO.getChannels()) {
            roomDO.setChannels(new ArrayList<>());
        }
        if (null == roomDO.getEncoderDevices()) {
            roomDO.setEncoderDevices(new ArrayList<>());
        }
        if (null == roomDO.getDecoderDevices()) {
            roomDO.setDecoderDevices(new ArrayList<>());
        }
        if (null == roomDO.getIpcDevices()) {
            roomDO.setIpcDevices(new ArrayList<>());
        }
        if (null == roomDO.getTvDevices()) {
            roomDO.setTvDevices(new ArrayList<>());
        }
        // 遍历设备列表，分类放到设备列表中
        for (DeviceDO deviceDO: deviceDOList) {
            // 将通道分类归置于设备中
            List<ChannelDO> deviceChannels = new ArrayList<>();
            for (ChannelDO channelDO: channelDOList) {
                if (deviceDO.getId().equals(channelDO.getDevice())) {
                    deviceChannels.add(channelDO);
                }
            }
            deviceDO.setChannels(deviceChannels);
            switch (deviceDO.getType()) {
                case PAD:
                    roomDO.setPad(deviceDO);
                    break;
                case ENCODER:
                    roomDO.getEncoderDevices().add(deviceDO);
                    // 将编码器通道加入到房间的通道列表中。
                    roomDO.getChannels().addAll(deviceDO.getChannels());
                    break;
                case DECODER:
                    deviceChannels.stream().sorted(Comparator.comparing(ChannelDO::getChannelId));
                    roomDO.getDecoderDevices().add(deviceDO);
                    break;
                case IPC:
                    roomDO.getIpcDevices().add(deviceDO);
                    // 将IPC通道加入到房间的通道列表中。
                    roomDO.getChannels().addAll(deviceDO.getChannels());
                    break;
                case TV:
                    roomDO.getTvDevices().add(deviceDO);
                    break;
                default:
                    break;
            }
        }

        // 给房间通道进行排序，用于展示手术室的通道列表。
        roomDO.getChannels().stream().sorted(Comparator.comparing(ChannelDO::getSort));
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
     * 根据设备MAC地址查询房间及房间绑定的设备信息
     *
     * @param mac 设备MAC地址
     * @return 房间及房间绑定的设备信息
     */
    public RoomDO getByMac(String mac) {
        DeviceDO deviceDO = this.deviceMapper.selectOne("mac", mac);
        RoomDO roomDO = this.roomMapper.selectById(deviceDO.getRoom());
        // 补充房间的设备与通道信息
        completeDeviceAndChannel(roomDO);
        return roomDO;
    }

    /**
     * 根据房间编号查询输出通道列表
     * @param roomId
     * @return
     */
    public List<ChannelDO> getOutputChannelsByRoom(Integer roomId) {
        List<ChannelDO> list = this.channelMapper.selectList(
                new QueryWrapperX<ChannelDO>().eq("room", roomId)
                        .eq("stream_type", StreamDirectionType.ENCODE_OUTPUT)
                        .orderByAsc("sort")
        );
        return list;
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

        // 从系统配置获取默认录像视频上线状态
        InfConfigDO infConfigDO = this.infConfigMapper.selectByKey(DEFAULT_OPERATION_VIDEO_ONLINE_STATUS);
        boolean defaultOnlineStatus = false;
        if (null != infConfigDO) {
            defaultOnlineStatus = Boolean.valueOf(infConfigDO.getValue());
        }

        OperationVideoDO operationVideoDO = OperationVideoDO.builder()
                .room(roomDO.getId())
                .title("未命名")
                .onlineStatus(defaultOnlineStatus)
                .build();
        int ret = this.operationVideoMapper.insert(operationVideoDO);
        logger.info("Save OperationVideoDO: {}, result: {}", operationVideoDO, ret);

        String poster = null;
        for (ChannelDO channel: channelDOList) {
            // 提交每个通道的录制。
            String relativePath = roomDO.getName() + File.separator + DateUtil.today() + File.separator +
                    channel.getName() + "-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String input = channel.getUrl();
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
                        id, roomDO.getName(), vf.getTaskId(), vf.getId(), result);

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
            boolean defaultOnlineStatus = false;
            if (null != infConfigDO) {
                defaultOnlineStatus = Boolean.valueOf(infConfigDO.getValue());
            }
            operationVideoDO.setOnlineStatus(defaultOnlineStatus);
            this.operationVideoMapper.updateById(operationVideoDO);

            // 更新房间录制状态至数据库。
            roomDO.setRecord(false);
            roomDO.setRecordVideo(null);
            this.roomMapper.updateById(roomDO);
        } else {
            logger.warn("{} record status is false!", roomDO.getName());
        }
    }

    @SneakyThrows
    private Result request(String method, JSONObject json) {
        try {
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
        } catch (Exception e) {
            logger.error("停止录制出错：", e);
            return null;
        }
    }

    @Data
    static class Result implements Serializable {
        private boolean success;
        private String data;
        private String msg;
    }
}
