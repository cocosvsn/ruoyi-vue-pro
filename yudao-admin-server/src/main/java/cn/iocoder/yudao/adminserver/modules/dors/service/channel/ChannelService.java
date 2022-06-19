package cn.iocoder.yudao.adminserver.modules.dors.service.channel;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 频道 Service 接口
 *
 * @author Bunco
 */
public interface ChannelService {

    /**
     * 创建频道
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer create(@Valid ChannelCreateReqVO createReqVO);

    /**
     * 更新频道
     *
     * @param updateReqVO 更新信息
     */
    void update(@Valid ChannelUpdateReqVO updateReqVO);

    /**
     * 删除频道
     *
     * @param id 编号
     */
    void delete(Integer id);

    /**
     * 获得频道
     *
     * @param id 编号
     * @return 频道
     */
    ChannelDO get(Integer id);

    /**
     * 获得频道列表
     *
     * @param ids 编号
     * @return 频道列表
     */
    List<ChannelDO> getList(Collection<Integer> ids);

    /**
     * 获得频道分页
     *
     * @param pageReqVO 分页查询
     * @return 频道分页
     */
    PageResult<ChannelDO> getPage(ChannelPageReqVO pageReqVO);

    /**
     * 获得频道列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 频道列表
     */
    List<ChannelDO> getList(ChannelExportReqVO exportReqVO);

    /**
     * 查询通道列表
     * @param pageReqVO
     * @return
     */
    List<ChannelDO> getList(ChannelPageReqVO pageReqVO);
}
