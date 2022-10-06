package cn.iocoder.yudao.adminserver.modules.dors.service.live;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 直播 Service 接口
 *
 * @author bunco
 */
public interface LiveService {

    /**
     * 创建直播
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createLive(@Valid LiveCreateReqVO createReqVO);

    /**
     * 更新直播
     *
     * @param updateReqVO 更新信息
     */
    void updateLive(@Valid LiveUpdateReqVO updateReqVO);

    /**
     * 删除直播
     *
     * @param id 编号
     */
    void deleteLive(Integer id);

    /**
     * 获得直播
     *
     * @param id 编号
     * @return 直播
     */
    LiveDO getLive(Integer id);

    /**
     * 获得直播列表
     *
     * @param ids 编号
     * @return 直播列表
     */
    List<LiveDO> getLiveList(Collection<Integer> ids);

    /**
     * 获得直播分页
     *
     * @param pageReqVO 分页查询
     * @return 直播分页
     */
    PageResult<LiveDO> getLivePage(LivePageReqVO pageReqVO);

    /**
     * 获得直播列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 直播列表
     */
    List<LiveDO> getLiveList(LiveExportReqVO exportReqVO);

}
