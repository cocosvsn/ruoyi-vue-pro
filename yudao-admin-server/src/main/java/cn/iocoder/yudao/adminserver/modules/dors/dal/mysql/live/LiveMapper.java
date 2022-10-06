package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.live;

import java.util.*;

import cn.iocoder.yudao.adminserver.modules.system.dal.dataobject.dept.SysDeptDO;
import cn.iocoder.yudao.adminserver.modules.system.dal.mysql.dept.SysDeptMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;

/**
 * 直播 Mapper
 *
 * @author bunco
 */
@Mapper
public interface LiveMapper extends BaseMapperX<LiveDO> {

    default PageResult<LiveDO> selectPage(LivePageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<LiveDO>()
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

    default List<LiveDO> selectList(LiveExportReqVO reqVO) {
        return selectList(new QueryWrapperX<LiveDO>()
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

}
