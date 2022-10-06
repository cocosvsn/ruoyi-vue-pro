package cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo.impl;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.videoFile.VideoFileMapper;
import cn.iocoder.yudao.framework.file.config.FileProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.util.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.adminserver.modules.dors.convert.operationVideo.OperationVideoConvert;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.operationVideo.OperationVideoMapper;
import cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo.OperationVideoService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;

/**
 * 手术视频 Service 实现类
 *
 * @author Bunco
 */
@Service
@Validated
public class OperationVideoServiceImpl implements OperationVideoService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private FileProperties fileProperties;
    @Resource
    private VideoFileMapper videoFileMapper;
    @Resource
    private OperationVideoMapper operationVideoMapper;

    @Override
    public Integer createOperationVideo(OperationVideoCreateReqVO createReqVO) {
        // 插入
        OperationVideoDO operationVideo = OperationVideoConvert.INSTANCE.convert(createReqVO);
        operationVideoMapper.insert(operationVideo);
        // 返回
        return operationVideo.getId();
    }

    @Override
    public void updateOperationVideo(OperationVideoUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateOperationVideoExists(updateReqVO.getId());
        // 更新
        OperationVideoDO updateObj = OperationVideoConvert.INSTANCE.convert(updateReqVO);
        operationVideoMapper.updateById(updateObj);
    }

    @Override
    public void deleteOperationVideo(Integer id) {
        // 校验存在
        this.validateOperationVideoExists(id);
        // 删除
        operationVideoMapper.deleteById(id);
        // 把相应通道的文件也删除，并回收磁盘空间。
        List<VideoFileDO> videoFileDOS = this.videoFileMapper.selectList("operation_video", id);
        for (VideoFileDO vf: videoFileDOS) {
            // 删除视频文件
            File channelVideoFile = new File(fileProperties.getLocal().getDirectory()+vf.getRelativePath());
            if (channelVideoFile.exists()) {
                logger.info("delete video file【{}】: {}", channelVideoFile.getAbsolutePath(), channelVideoFile.delete());
            }
            // 删除海报文件
            File channelPosterFile = new File(
                    StringUtils.substringBeforeLast(channelVideoFile.getAbsolutePath(), ".")+".jpg");
            if (channelPosterFile.exists()) {
                logger.info("delete poster file【{}】: {}", channelPosterFile.getAbsolutePath(), channelPosterFile.delete());
            }
            // 删除数据库记录
            this.videoFileMapper.deleteById(vf.getId());
        }
    }

    private void validateOperationVideoExists(Integer id) {
        if (operationVideoMapper.selectById(id) == null) {
            throw exception(OPERATION_VIDEO_NOT_EXISTS);
        }
    }

    @Override
    public OperationVideoDO getOperationVideo(Integer id) {
        return operationVideoMapper.selectById(id);
    }

    @Override
    public List<OperationVideoDO> getOperationVideoList(Collection<Integer> ids) {
        return operationVideoMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<OperationVideoDO> getOperationVideoPage(OperationVideoPageReqVO pageReqVO, List<Long> deptIds) {
        PageResult<OperationVideoDO> pageResult = operationVideoMapper.selectPage(pageReqVO, deptIds);

        pageResult.getList().forEach(ov -> {
            ov.setVideoFiles(videoFileMapper.selectList("operation_video", ov.getId()));
        });
        return pageResult;
    }

    @Override
    public List<OperationVideoDO> getOperationVideoList(OperationVideoExportReqVO exportReqVO) {
        return operationVideoMapper.selectList(exportReqVO);
    }

}
