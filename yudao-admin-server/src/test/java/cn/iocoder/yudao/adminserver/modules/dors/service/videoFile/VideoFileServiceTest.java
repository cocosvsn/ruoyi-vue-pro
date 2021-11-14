package cn.iocoder.yudao.adminserver.modules.dors.service.videoFile;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.videoFile.impl.VideoFileServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.videoFile.VideoFileMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;
import java.util.*;

import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomInteger;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.*;

/**
* {@link VideoFileServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(VideoFileServiceImpl.class)
public class VideoFileServiceTest extends BaseDbUnitTest {

    @Resource
    private VideoFileServiceImpl videoFileService;

    @Resource
    private VideoFileMapper videoFileMapper;

    @Test
    public void testCreateVideoFile_success() {
        // 准备参数
        VideoFileCreateReqVO reqVO = randomPojo(VideoFileCreateReqVO.class);

        // 调用
        Integer videoFileId = videoFileService.createVideoFile(reqVO);
        // 断言
        assertNotNull(videoFileId);
        // 校验记录的属性是否正确
        VideoFileDO videoFile = videoFileMapper.selectById(videoFileId);
        assertPojoEquals(reqVO, videoFile);
    }

    @Test
    public void testUpdateVideoFile_success() {
        // mock 数据
        VideoFileDO dbVideoFile = randomPojo(VideoFileDO.class);
        videoFileMapper.insert(dbVideoFile);// @Sql: 先插入出一条存在的数据
        // 准备参数
        VideoFileUpdateReqVO reqVO = randomPojo(VideoFileUpdateReqVO.class, o -> {
            o.setId(dbVideoFile.getId()); // 设置更新的 ID
        });

        // 调用
        videoFileService.updateVideoFile(reqVO);
        // 校验是否更新正确
        VideoFileDO videoFile = videoFileMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, videoFile);
    }

    @Test
    public void testUpdateVideoFile_notExists() {
        // 准备参数
        VideoFileUpdateReqVO reqVO = randomPojo(VideoFileUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> videoFileService.updateVideoFile(reqVO), VIDEO_FILE_NOT_EXISTS);
    }

    @Test
    public void testDeleteVideoFile_success() {
        // mock 数据
        VideoFileDO dbVideoFile = randomPojo(VideoFileDO.class);
        videoFileMapper.insert(dbVideoFile);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbVideoFile.getId();

        // 调用
        videoFileService.deleteVideoFile(id);
       // 校验数据不存在了
       assertNull(videoFileMapper.selectById(id));
    }

    @Test
    public void testDeleteVideoFile_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> videoFileService.deleteVideoFile(id), VIDEO_FILE_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetVideoFilePage() {
       // mock 数据
       VideoFileDO dbVideoFile = randomPojo(VideoFileDO.class, o -> { // 等会查询到
           o.setOperationVideo(null);
           o.setTitle(null);
           o.setContentType(null);
           o.setCreateTime(null);
       });
       videoFileMapper.insert(dbVideoFile);
       // 测试 operationVideo 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setOperationVideo(null)));
       // 测试 title 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setTitle(null)));
       // 测试 contentType 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setContentType(null)));
       // 测试 createTime 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setCreateTime(null)));
       // 准备参数
       VideoFilePageReqVO reqVO = new VideoFilePageReqVO();
       reqVO.setOperationVideo(null);
       reqVO.setTitle(null);
       reqVO.setContentType(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       PageResult<VideoFileDO> pageResult = videoFileService.getVideoFilePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbVideoFile, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetVideoFileList() {
       // mock 数据
       VideoFileDO dbVideoFile = randomPojo(VideoFileDO.class, o -> { // 等会查询到
           o.setOperationVideo(null);
           o.setTitle(null);
           o.setContentType(null);
           o.setCreateTime(null);
       });
       videoFileMapper.insert(dbVideoFile);
       // 测试 operationVideo 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setOperationVideo(null)));
       // 测试 title 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setTitle(null)));
       // 测试 contentType 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setContentType(null)));
       // 测试 createTime 不匹配
       videoFileMapper.insert(ObjectUtils.clone(dbVideoFile, o -> o.setCreateTime(null)));
       // 准备参数
       VideoFileExportReqVO reqVO = new VideoFileExportReqVO();
       reqVO.setOperationVideo(null);
       reqVO.setTitle(null);
       reqVO.setContentType(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       List<VideoFileDO> list = videoFileService.getVideoFileList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbVideoFile, list.get(0));
    }

}
