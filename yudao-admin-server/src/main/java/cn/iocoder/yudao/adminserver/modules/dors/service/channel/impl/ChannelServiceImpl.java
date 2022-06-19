package cn.iocoder.yudao.adminserver.modules.dors.service.channel.impl;

import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.channel.ChannelConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.channel.ChannelMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.channel.ChannelService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 频道 Service 实现类
 *
 * @author Bunco
 */
@Service
@Validated
public class ChannelServiceImpl implements ChannelService {

    @Resource
    private ChannelMapper channelMapper;

    @Override
    public Integer create(ChannelCreateReqVO createReqVO) {
        // 插入
        ChannelDO channelDO = ChannelConvert.INSTANCE.convert(createReqVO);
        channelMapper.insert(channelDO);
        // 返回
        return channelDO.getId();
    }

    @Override
    public void update(ChannelUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateExists(updateReqVO.getId());
        // 更新
        ChannelDO updateObj = ChannelConvert.INSTANCE.convert(updateReqVO);
        channelMapper.updateById(updateObj);
    }

    @Override
    public void delete(Integer id) {
        // 校验存在
        this.validateExists(id);
        // 删除
        channelMapper.deleteById(id);
    }

    private void validateExists(Integer id) {
        if (channelMapper.selectById(id) == null) {
            throw exception(CHANNEL_NOT_EXISTS);
        }
    }

    @Override
    public ChannelDO get(Integer id) {
        return channelMapper.selectById(id);
    }

    @Override
    public List<ChannelDO> getList(Collection<Integer> ids) {
        return channelMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ChannelDO> getPage(ChannelPageReqVO pageReqVO) {
        return channelMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ChannelDO> getList(ChannelExportReqVO exportReqVO) {
        return channelMapper.selectList(exportReqVO);
    }

    /**
     * 查询通道列表
     * @param pageReqVO
     * @return
     */
    public List<ChannelDO> getList(ChannelPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = new QueryWrapperX<ChannelDO>()
                .eqIfPresent("room", pageReqVO.getRoom())
                .eqIfPresent("type", pageReqVO.getType())
                .eqIfPresent("stream_type", pageReqVO.getStreamType().name())
                .likeIfPresent("name", pageReqVO.getName())
                .betweenIfPresent("create_time", pageReqVO.getBeginCreateTime(), pageReqVO.getEndCreateTime())
                .orderByAsc("channel_id");
        return channelMapper.selectList(queryWrapper);
    }
}
