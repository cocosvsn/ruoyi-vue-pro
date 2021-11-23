package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.operationVideo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;

/**
 * 手术视频 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface OperationVideoMapper extends BaseMapperX<OperationVideoDO> {

    default PageResult<OperationVideoDO> selectPage(OperationVideoPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<OperationVideoDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("title", reqVO.getTitle())
                .eqIfPresent("doctor", reqVO.getDoctor())
                .eqIfPresent("patient", reqVO.getPatient())
                .eqIfPresent("operation_info", reqVO.getOperationInfo())
                .eqIfPresent("online_status", reqVO.getOnlineStatus())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

    default List<OperationVideoDO> selectList(OperationVideoExportReqVO reqVO) {
        return selectList(new QueryWrapperX<OperationVideoDO>()
                .eqIfPresent("room", reqVO.getRoom())
                .eqIfPresent("title", reqVO.getTitle())
                .eqIfPresent("doctor", reqVO.getDoctor())
                .eqIfPresent("patient", reqVO.getPatient())
                .eqIfPresent("operation_info", reqVO.getOperationInfo())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

}
