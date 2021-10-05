package cn.iocoder.yudao.adminserver.modules.dors.service.version;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 应用版本 Service 接口
 *
 * @author Bunco
 */
public interface VersionService {

    /**
     * 创建应用版本
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer create(@Valid VersionCreateReqVO createReqVO);

    /**
     * 更新应用版本
     *
     * @param updateReqVO 更新信息
     */
    void update(@Valid VersionUpdateReqVO updateReqVO);

    /**
     * 删除应用版本
     *
     * @param id 编号
     */
    void delete(Integer id);

    /**
     * 获得应用版本
     *
     * @param id 编号
     * @return 应用版本
     */
    VersionDO get(Integer id);

    /**
     * 获得应用版本列表
     *
     * @param ids 编号
     * @return 应用版本列表
     */
    List<VersionDO> getList(Collection<Integer> ids);

    /**
     * 获得应用版本分页
     *
     * @param pageReqVO 分页查询
     * @return 应用版本分页
     */
    PageResult<VersionDO> getPage(VersionPageReqVO pageReqVO);

    /**
     * 获得应用版本列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用版本列表
     */
    List<VersionDO> getList(VersionExportReqVO exportReqVO);

    /**
     * 根据应用包名，获取指定应用最新版本信息
     * @param packageName 应用包名（应用唯一标识）
     * @return
     */
    VersionDO getLatestVersion(String packageName);
}
