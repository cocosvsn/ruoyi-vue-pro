package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.channel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;

/**
 * 频道 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface ChannelMapper extends BaseMapperX<ChannelDO> {

    default PageResult<ChannelDO> selectPage(ChannelPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<ChannelDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("type", reqVO.getType())
                .eqIfPresent("stream_type", reqVO.getStreamType())
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")
        );
    }

    default List<ChannelDO> selectList(ChannelExportReqVO reqVO) {
        return selectList(new QueryWrapperX<ChannelDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("type", reqVO.getType())
                .eqIfPresent("stream_type", reqVO.getStreamType())
                .likeIfPresent("name", reqVO.getName())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")
        );
    }

}
