package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.device;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;

/**
 * 设备 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface DeviceMapper extends BaseMapperX<DeviceDO> {

    default PageResult<DeviceDO> selectPage(DevicePageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<DeviceDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("type", reqVO.getType())
                .eqIfPresent("serial_no", reqVO.getSerialNo())
                .eqIfPresent("device_mac", reqVO.getDeviceMac())
                .eqIfPresent("app_version", reqVO.getAppVersion())
                .eqIfPresent("sdk_version", reqVO.getSdkVersion())
                .eqIfPresent("sys_version", reqVO.getSysVersion())
                .eqIfPresent("last_online_ip", reqVO.getLastOnlineIp())
                .betweenIfPresent("last_online_time", reqVO.getBeginLastOnlineTime(), reqVO.getEndLastOnlineTime())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .eqIfPresent("remarks", reqVO.getRemarks())
                .orderByDesc("id")        );
    }

    default List<DeviceDO> selectList(DeviceExportReqVO reqVO) {
        return selectList(new QueryWrapperX<DeviceDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("type", reqVO.getType())
                .eqIfPresent("serial_no", reqVO.getSerialNo())
                .eqIfPresent("device_mac", reqVO.getDeviceMac())
                .eqIfPresent("app_version", reqVO.getAppVersion())
                .eqIfPresent("sdk_version", reqVO.getSdkVersion())
                .eqIfPresent("sys_version", reqVO.getSysVersion())
                .eqIfPresent("last_online_ip", reqVO.getLastOnlineIp())
                .betweenIfPresent("last_online_time", reqVO.getBeginLastOnlineTime(), reqVO.getEndLastOnlineTime())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .eqIfPresent("remarks", reqVO.getRemarks())
                .orderByDesc("id")        );
    }

}
