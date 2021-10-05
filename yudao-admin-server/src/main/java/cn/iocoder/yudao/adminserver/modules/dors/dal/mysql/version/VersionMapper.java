package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.version;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;

/**
 * 应用版本 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface VersionMapper extends BaseMapperX<VersionDO> {

    default PageResult<VersionDO> selectPage(VersionPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<VersionDO>()
                .likeIfPresent("name", reqVO.getName())
                .likeIfPresent("package_name", reqVO.getPackageName())
                .eqIfPresent("version_num", reqVO.getVersionNum())
                .eqIfPresent("version_code", reqVO.getVersionCode())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

    default List<VersionDO> selectList(VersionExportReqVO reqVO) {
        return selectList(new QueryWrapperX<VersionDO>()
                .likeIfPresent("name", reqVO.getName())
                .likeIfPresent("package_name", reqVO.getPackageName())
                .eqIfPresent("version_num", reqVO.getVersionNum())
                .eqIfPresent("version_code", reqVO.getVersionCode())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }
}
