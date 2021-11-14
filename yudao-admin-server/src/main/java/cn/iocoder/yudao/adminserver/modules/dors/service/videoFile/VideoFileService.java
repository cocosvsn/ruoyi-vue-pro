package cn.iocoder.yudao.adminserver.modules.dors.service.videoFile;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 手术视频文件 Service 接口
 *
 * @author Bunco
 */
public interface VideoFileService {

    /**
     * 创建手术视频文件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createVideoFile(@Valid VideoFileCreateReqVO createReqVO);

    /**
     * 更新手术视频文件
     *
     * @param updateReqVO 更新信息
     */
    void updateVideoFile(@Valid VideoFileUpdateReqVO updateReqVO);

    /**
     * 删除手术视频文件
     *
     * @param id 编号
     */
    void deleteVideoFile(Integer id);

    /**
     * 获得手术视频文件
     *
     * @param id 编号
     * @return 手术视频文件
     */
    VideoFileDO getVideoFile(Integer id);

    /**
     * 获得手术视频文件列表
     *
     * @param ids 编号
     * @return 手术视频文件列表
     */
    List<VideoFileDO> getVideoFileList(Collection<Integer> ids);

    /**
     * 获得手术视频文件分页
     *
     * @param pageReqVO 分页查询
     * @return 手术视频文件分页
     */
    PageResult<VideoFileDO> getVideoFilePage(VideoFilePageReqVO pageReqVO);

    /**
     * 获得手术视频文件列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 手术视频文件列表
     */
    List<VideoFileDO> getVideoFileList(VideoFileExportReqVO exportReqVO);

}
