package cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.videoFile;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;

/**
 * 手术视频文件 Mapper
 *
 * @author Bunco
 */
@Mapper
public interface VideoFileMapper extends BaseMapperX<VideoFileDO> {

    default PageResult<VideoFileDO> selectPage(VideoFilePageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<VideoFileDO>()
                .eqIfPresent("operation_video", reqVO.getOperationVideo())
                .eqIfPresent("title", reqVO.getTitle())
                .eqIfPresent("content_type", reqVO.getContentType())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

    default List<VideoFileDO> selectList(VideoFileExportReqVO reqVO) {
        return selectList(new QueryWrapperX<VideoFileDO>()
                .eqIfPresent("operation_video", reqVO.getOperationVideo())
                .eqIfPresent("title", reqVO.getTitle())
                .eqIfPresent("content_type", reqVO.getContentType())
                .betweenIfPresent("create_time", reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc("id")        );
    }

}
