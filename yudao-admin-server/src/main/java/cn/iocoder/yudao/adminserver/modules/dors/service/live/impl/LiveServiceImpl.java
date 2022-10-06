package cn.iocoder.yudao.adminserver.modules.dors.service.live.impl;

import cn.iocoder.yudao.adminserver.modules.system.dal.dataobject.dept.SysDeptDO;
import cn.iocoder.yudao.adminserver.modules.system.dal.dataobject.user.SysUserDO;
import cn.iocoder.yudao.adminserver.modules.system.dal.mysql.dept.SysDeptMapper;
import cn.iocoder.yudao.adminserver.modules.system.dal.mysql.user.SysUserMapper;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.live.LiveConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.live.LiveMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.live.LiveService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 直播 Service 实现类
 *
 * @author bunco
 */
@Service
@Validated
public class LiveServiceImpl implements LiveService {

    @Resource
    private LiveMapper liveMapper;
    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;

    @Override
    public Integer createLive(LiveCreateReqVO createReqVO) {
        // 插入
        LiveDO live = LiveConvert.INSTANCE.convert(createReqVO);
        liveMapper.insert(live);
        // 返回
        return live.getId();
    }

    @Override
    public void updateLive(LiveUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateLiveExists(updateReqVO.getId());
        // 更新
        LiveDO updateObj = LiveConvert.INSTANCE.convert(updateReqVO);
        liveMapper.updateById(updateObj);
    }

    @Override
    public void deleteLive(Integer id) {
        // 校验存在
        LiveDO live = this.liveMapper.selectById(id);
        if (null != live) {
            // TODO 删除图片
            // 删除
            liveMapper.deleteById(id);
        }
    }

    private void validateLiveExists(Integer id) {
        if (liveMapper.selectById(id) == null) {
            throw exception(LIVE_NOT_EXISTS);
        }
    }

    @Override
    public LiveDO getLive(Integer id) {
        return liveMapper.selectById(id);
    }

    @Override
    public List<LiveDO> getLiveList(Collection<Integer> ids) {
        return liveMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LiveDO> getLivePage(LivePageReqVO pageReqVO) {
        SysUserDO userDO = this.userMapper.selectById(WebFrameworkUtils.getLoginUserId());
        List<Object> deptIds = this.deptMapper.selectObjs(new QueryWrapper<SysDeptDO>().select("id").eq("parent_id", userDO.getDeptId()));
        return liveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LiveDO> getLiveList(LiveExportReqVO exportReqVO) {
        return liveMapper.selectList(exportReqVO);
    }

}
