package cn.iocoder.yudao.adminserver.modules.dors.convert.videoFile;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;

/**
 * 手术视频文件 Convert
 *
 * @author Bunco
 */
@Mapper
public interface VideoFileConvert {

    VideoFileConvert INSTANCE = Mappers.getMapper(VideoFileConvert.class);

    VideoFileDO convert(VideoFileCreateReqVO bean);

    VideoFileDO convert(VideoFileUpdateReqVO bean);

    VideoFileRespVO convert(VideoFileDO bean);

    List<VideoFileRespVO> convertList(List<VideoFileDO> list);

    PageResult<VideoFileRespVO> convertPage(PageResult<VideoFileDO> page);

    List<VideoFileExcelVO> convertList02(List<VideoFileDO> list);

}
