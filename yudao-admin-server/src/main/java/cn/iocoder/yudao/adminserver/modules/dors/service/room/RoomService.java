package cn.iocoder.yudao.adminserver.modules.dors.service.room;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 房间 Service 接口
 *
 * @author Bunco
 */
public interface RoomService {

    /**
     * 创建房间
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer create(@Valid RoomCreateReqVO createReqVO);

    /**
     * 更新房间
     *
     * @param updateReqVO 更新信息
     */
    void update(@Valid RoomUpdateReqVO updateReqVO);

    /**
     * 删除房间
     *
     * @param id 编号
     */
    void delete(Integer id);

    /**
     * 获得房间
     *
     * @param id 编号
     * @return 房间
     */
    RoomDO get(Integer id);

    /**
     * 获得房间列表
     *
     * @param ids 编号
     * @return 房间列表
     */
    List<RoomDO> getList(Collection<Integer> ids);

    /**
     * 获得房间分页
     *
     * @param pageReqVO 分页查询
     * @return 房间分页
     */
    PageResult<RoomDO> getPage(RoomPageReqVO pageReqVO);

    /**
     * 获得房间列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 房间列表
     */
    List<RoomDO> getList(RoomExportReqVO exportReqVO);

    /**
     * 获得手术室房间列表
     * @return 房间列表
     */
    List<RoomDO> getOperatingRoomList();

    /**
     * 启动指定房间视频通道列表录制
     * @param id 房间编号
     * @param channelIds 通道编号列表
     * @return
     */
    boolean startRecord(Integer id, List<Integer> channelIds);

    /**
     * 停止指定房间视频录制
     * @param id 房间编号
     * @return
     */
    void stopRecord(Integer id);
}
