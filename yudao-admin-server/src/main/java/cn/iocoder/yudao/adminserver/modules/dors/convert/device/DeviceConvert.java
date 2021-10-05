package cn.iocoder.yudao.adminserver.modules.dors.convert.device;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;

/**
 * 设备 Convert
 *
 * @author Bunco
 */
@Mapper
public interface DeviceConvert {

    DeviceConvert INSTANCE = Mappers.getMapper(DeviceConvert.class);

    DeviceDO convert(DeviceCreateReqVO bean);

    DeviceDO convert(DeviceUpdateReqVO bean);

    DeviceRespVO convert(DeviceDO bean);

    List<DeviceRespVO> convertList(List<DeviceDO> list);

    PageResult<DeviceRespVO> convertPage(PageResult<DeviceDO> page);

    List<DeviceExcelVO> convertList02(List<DeviceDO> list);

}
