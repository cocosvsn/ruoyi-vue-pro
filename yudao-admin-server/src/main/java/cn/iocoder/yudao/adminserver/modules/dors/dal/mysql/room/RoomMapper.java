package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.room;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import org.apache.ibatis.annotations.Select;

/**
 * 房间 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface RoomMapper extends BaseMapperX<RoomDO> {

    default PageResult<RoomDO> selectPage(RoomPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<RoomDO>()
                .eqIfPresent("type", reqVO.getType())
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .eqIfPresent("remarks", reqVO.getRemarks())
                .orderByDesc("id")        );
    }

    default List<RoomDO> selectList(RoomExportReqVO reqVO) {
        return selectList(new QueryWrapperX<RoomDO>()
                .eqIfPresent("type", reqVO.getType())
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .eqIfPresent("remarks", reqVO.getRemarks())
                .orderByDesc("id")        );
    }

    /**
     * 根据设备MAC地址查询房间及房间绑定的设备信息
     *
     * @param mac 设备MAC地址
     * @return 房间及房间绑定的设备信息
     */
    @Select("SELECT * FROM dors_room r LEFT JOIN dors_device d ON r.id = d.room WHERE r.id = (SELECT d2.id FROM dors_device d2 WHERE d2.device_mac = #{maxUpdateTime} LIMIT 1)")
    RoomDO selectByDeviceMac(String mac);
}
