package cn.iocoder.yudao.adminserver.modules.dors.service.version.impl;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.version.VersionConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.version.VersionMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.version.VersionService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 应用版本 Service 实现类
 *
 * @author Bunco
 */
@Service
@Validated
public class VersionServiceImpl implements VersionService {

    @Resource
    private VersionMapper versionMapper;

    @Override
    public Integer create(VersionCreateReqVO createReqVO) {
        // 插入
        VersionDO versionDO = VersionConvert.INSTANCE.convert(createReqVO);
        versionMapper.insert(versionDO);
        // 返回
        return versionDO.getId();
    }

    @Override
    public void update(VersionUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateExists(updateReqVO.getId());
        // 更新
        VersionDO updateObj = VersionConvert.INSTANCE.convert(updateReqVO);
        versionMapper.updateById(updateObj);
    }

    @Override
    public void delete(Integer id) {
        // 校验存在
        this.validateExists(id);
        // 删除
        versionMapper.deleteById(id);
    }

    private void validateExists(Integer id) {
        if (versionMapper.selectById(id) == null) {
            throw exception(VERSION_NOT_EXISTS);
        }
    }

    @Override
    public VersionDO get(Integer id) {
        return versionMapper.selectById(id);
    }

    @Override
    public List<VersionDO> getList(Collection<Integer> ids) {
        return versionMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<VersionDO> getPage(VersionPageReqVO pageReqVO) {
        return versionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<VersionDO> getList(VersionExportReqVO exportReqVO) {
        return versionMapper.selectList(exportReqVO);
    }

    /**
     * 根据应用包名，获取指定应用最新版本信息
     * @param packageName 应用包名（应用唯一标识）
     * @return
     */
    public VersionDO getLatestVersion(String packageName) {
        VersionDO versionDO = this.versionMapper.selectOne(new QueryWrapperX<VersionDO>()
                .eq("package_name", packageName)
                .orderByDesc("version_num")
                .last("limit 1")
        );
        return versionDO;
    }
}
