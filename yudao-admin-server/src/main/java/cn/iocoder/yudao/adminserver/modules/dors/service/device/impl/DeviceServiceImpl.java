package cn.iocoder.yudao.adminserver.modules.dors.service.device.impl;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDiscovery;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.channel.ChannelMapper;
import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import cn.iocoder.yudao.adminserver.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.yudao.adminserver.modules.infra.dal.mysql.config.InfConfigMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.udpserver.core.service.MessageProcessor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.device.DeviceConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.device.DeviceMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.device.DeviceService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 设备 Service 实现类
 *
 * @author Bunco
 */
@Slf4j
@Service
@Validated
public class DeviceServiceImpl implements DeviceService, MessageProcessor {
    private static Set<String> discoveryDevice = new HashSet<>();

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ChannelMapper channelMapper;
    @Resource
    private InfConfigMapper configMapper;


    @Override
    public Integer create(DeviceCreateReqVO createReqVO) {
        // 插入
        DeviceDO deviceDO = DeviceConvert.INSTANCE.convert(createReqVO);
        deviceMapper.insert(deviceDO);
        // 返回
        return deviceDO.getId();
    }

    @Override
    public void update(DeviceUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateExists(updateReqVO.getId());
        // 更新
        DeviceDO updateObj = DeviceConvert.INSTANCE.convert(updateReqVO);
        if (null != updateObj.getRoom()) {
            // 修改房间时，需要将原来房间绑定的设备的所属房间置空
            List<DeviceDO> list = deviceMapper.selectList(new QueryWrapperX<DeviceDO>()
                    .eq("room", updateObj.getRoom())
                    .eq("type", updateObj.getType().name()));
            for (DeviceDO d: list) {
                d.setRoom(null);
                this.deviceMapper.updateById(d);
            }
        }
        deviceMapper.updateById(updateObj);
    }

    @Override
    public void delete(Integer id) {
        // 校验存在
        this.validateExists(id);
        // 删除
        deviceMapper.deleteById(id);
        // 删除设备时，将绑定该设备的通道列表一并删除。
        this.channelMapper.delete(new QueryWrapper<ChannelDO>().eq("device", id));
    }

    private void validateExists(Integer id) {
        if (deviceMapper.selectById(id) == null) {
            throw exception(DEVICE_NOT_EXISTS);
        }
    }

    @Override
    public DeviceDO get(Integer id) {
        return deviceMapper.selectById(id);
    }

    @Override
    public List<DeviceDO> getList(Collection<Integer> ids) {
        return deviceMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<DeviceDO> getPage(DevicePageReqVO pageReqVO) {
        return deviceMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DeviceDO> getList(DeviceExportReqVO exportReqVO) {
        return deviceMapper.selectList(exportReqVO);
    }

    /**
     * 重新扫描设备
     */
    public void rediscover() {
        discoveryDevice.clear();
    }

    @Override
    @Async
    public void handlerMessage(String message, Map<String, Object> headers) {
        try {
            DeviceDiscovery deviceDiscovery = new ObjectMapper().readValue(message, DeviceDiscovery.class);
            if (discoveryDevice.contains(deviceDiscovery.getFrom())) { // 已发现设备列表中存在，则返回。不做处理
                return;
            }
            String fromIp = headers.get("ip_address").toString();
            DeviceDO deviceDO = deviceMapper.selectOne("device_mac", deviceDiscovery.getFrom());
            if (null == deviceDO) { // 新增
                deviceDO = new DeviceDO();
                deviceDO.setDeviceMac(deviceDiscovery.getFrom())
                        .setType(deviceDiscovery.getDeviceType())
                        .setAppVersion(deviceDiscovery.getAppVersion())
                        .setSdkVersion(deviceDiscovery.getSdkVersion())
                        .setSysVersion(deviceDiscovery.getSysVersion())
                        .setLastOnlineIp(fromIp);
                log.info("{}", deviceDO);
                this.deviceMapper.insert(deviceDO);
            } else if (!StringUtils.equals(deviceDO.getAppVersion(), deviceDiscovery.getAppVersion())
                    || !StringUtils.equals(deviceDO.getSdkVersion(), deviceDiscovery.getSdkVersion())
                    || !StringUtils.equals(deviceDO.getSysVersion(), deviceDiscovery.getSysVersion())
                    || !StringUtils.equals(deviceDO.getLastOnlineIp(), fromIp)) {
                // app, sdk, sys, ip 只要有一项发生变更即需要更新信息
                if (deviceDO.getDeleted()) { // 重新发现，如果是删除的设备则需要更新删除状态
                    deviceDO.setDeleted(false);
                }
                // 更新信息
                deviceDO.setAppVersion(deviceDiscovery.getAppVersion());
                deviceDO.setSdkVersion(deviceDiscovery.getSdkVersion());
                deviceDO.setSysVersion(deviceDiscovery.getSysVersion());
                deviceDO.setLastOnlineIp(fromIp);
                this.deviceMapper.updateById(deviceDO);
            }
            // 灵派编码器获取通道信息
            if (DeviceType.C3531D.equals(deviceDiscovery.getDeviceType())) {
                getDeviceChannelInfo(fromIp, deviceDO);
            }
            // 放入已发现设备列表中，避免重复查库
            discoveryDevice.add(deviceDiscovery.getFrom());
        } catch (JsonProcessingException e) {
            log.error("JSON格式UDP报文解析失败：", e);
        } catch (IOException e) {
            log.error("获取设备通道信息失败：", e);
        }
    }

    /**
     * 获取通道信息
     * @param ip
     */
    private void getDeviceChannelInfo(String ip, DeviceDO device) throws IOException {
        InfConfigDO configDO = this.configMapper.selectByKey(device.getType().name() + ".url");
        String url = MessageFormat.format(configDO.getValue(), ip);
        JsonNode jsonNode = new ObjectMapper().readTree(new URL(url));
        log.info("{}", jsonNode);
        jsonNode.forEach(channel -> {
            ChannelDO channelDO = this.channelMapper.selectOne(new QueryWrapper<ChannelDO>().lambda()
                    .and(qw -> qw.eq(ChannelDO::getDevice, device.getId()))
                    .and(qw -> qw.eq(ChannelDO::getChannelId, channel.get("id").intValue())));
            boolean isEnable = channel.get("enable").booleanValue();
            log.info("{}", channel);
            if (null == channelDO) {
                channelDO = new ChannelDO();
                channelDO.setChannelId(channel.get("id").intValue())
                        .setDevice(device.getId())
                        .setType(channel.get("type").textValue())
                        .setName(channel.get("name").textValue())
                        .setJsonInfo(channel.toString())
                        .setDisplay(isEnable);
                this.channelMapper.insert(channelDO);
            } else {
                channelDO.setType(channel.get("type").textValue())
//                        .setName(channel.get("name").textValue())
                        .setJsonInfo(channel.toString())
                        .setDisplay(isEnable);
                this.channelMapper.updateById(channelDO);
            }
        });
    }
}
