package cn.iocoder.yudao.adminserver.modules.dors.service.room.impl;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.device.DeviceMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.util.*;
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

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private DeviceMapper deviceMapper;

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
}
