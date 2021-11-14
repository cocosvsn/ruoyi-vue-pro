package cn.iocoder.yudao.adminserver.modules.dors.service.videoFile.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.videoFile.VideoFileConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.videoFile.VideoFileMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.videoFile.VideoFileService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 手术视频文件 Service 实现类
 *
 * @author Bunco
 */
@Service
@Validated
public class VideoFileServiceImpl implements VideoFileService {

    @Resource
    private VideoFileMapper videoFileMapper;

    @Override
    public Integer createVideoFile(VideoFileCreateReqVO createReqVO) {
        // 插入
        VideoFileDO videoFile = VideoFileConvert.INSTANCE.convert(createReqVO);
        videoFileMapper.insert(videoFile);
        // 返回
        return videoFile.getId();
    }

    @Override
    public void updateVideoFile(VideoFileUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateVideoFileExists(updateReqVO.getId());
        // 更新
        VideoFileDO updateObj = VideoFileConvert.INSTANCE.convert(updateReqVO);
        videoFileMapper.updateById(updateObj);
    }

    @Override
    public void deleteVideoFile(Integer id) {
        // 校验存在
        this.validateVideoFileExists(id);
        // 删除
        videoFileMapper.deleteById(id);
    }

    private void validateVideoFileExists(Integer id) {
        if (videoFileMapper.selectById(id) == null) {
            throw exception(VIDEO_FILE_NOT_EXISTS);
        }
    }

    @Override
    public VideoFileDO getVideoFile(Integer id) {
        return videoFileMapper.selectById(id);
    }

    @Override
    public List<VideoFileDO> getVideoFileList(Collection<Integer> ids) {
        return videoFileMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<VideoFileDO> getVideoFilePage(VideoFilePageReqVO pageReqVO) {
        return videoFileMapper.selectPage(pageReqVO);
    }

    @Override
    public List<VideoFileDO> getVideoFileList(VideoFileExportReqVO exportReqVO) {
        return videoFileMapper.selectList(exportReqVO);
    }

}
