package cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo.impl.OperationVideoServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.operationVideo.OperationVideoMapper;
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
* {@link OperationVideoServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(OperationVideoServiceImpl.class)
public class OperationVideoServiceTest extends BaseDbUnitTest {

    @Resource
    private OperationVideoServiceImpl operationVideoService;

    @Resource
    private OperationVideoMapper operationVideoMapper;

    @Test
    public void testCreateOperationVideo_success() {
        // 准备参数
        OperationVideoCreateReqVO reqVO = randomPojo(OperationVideoCreateReqVO.class);

        // 调用
        Integer operationVideoId = operationVideoService.createOperationVideo(reqVO);
        // 断言
        assertNotNull(operationVideoId);
        // 校验记录的属性是否正确
        OperationVideoDO operationVideo = operationVideoMapper.selectById(operationVideoId);
        assertPojoEquals(reqVO, operationVideo);
    }

    @Test
    public void testUpdateOperationVideo_success() {
        // mock 数据
        OperationVideoDO dbOperationVideo = randomPojo(OperationVideoDO.class);
        operationVideoMapper.insert(dbOperationVideo);// @Sql: 先插入出一条存在的数据
        // 准备参数
        OperationVideoUpdateReqVO reqVO = randomPojo(OperationVideoUpdateReqVO.class, o -> {
            o.setId(dbOperationVideo.getId()); // 设置更新的 ID
        });

        // 调用
        operationVideoService.updateOperationVideo(reqVO);
        // 校验是否更新正确
        OperationVideoDO operationVideo = operationVideoMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, operationVideo);
    }

    @Test
    public void testUpdateOperationVideo_notExists() {
        // 准备参数
        OperationVideoUpdateReqVO reqVO = randomPojo(OperationVideoUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> operationVideoService.updateOperationVideo(reqVO), OPERATION_VIDEO_NOT_EXISTS);
    }

    @Test
    public void testDeleteOperationVideo_success() {
        // mock 数据
        OperationVideoDO dbOperationVideo = randomPojo(OperationVideoDO.class);
        operationVideoMapper.insert(dbOperationVideo);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbOperationVideo.getId();

        // 调用
        operationVideoService.deleteOperationVideo(id);
       // 校验数据不存在了
       assertNull(operationVideoMapper.selectById(id));
    }

    @Test
    public void testDeleteOperationVideo_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> operationVideoService.deleteOperationVideo(id), OPERATION_VIDEO_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetOperationVideoPage() {
       // mock 数据
       OperationVideoDO dbOperationVideo = randomPojo(OperationVideoDO.class, o -> { // 等会查询到
           o.setRoom(null);
           o.setTitle(null);
           o.setDoctor(null);
           o.setPatient(null);
           o.setOperationInfo(null);
           o.setCreateTime(null);
       });
       operationVideoMapper.insert(dbOperationVideo);
       // 测试 room 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setRoom(null)));
       // 测试 title 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setTitle(null)));
       // 测试 doctor 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setDoctor(null)));
       // 测试 patient 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setPatient(null)));
       // 测试 operationInfo 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setOperationInfo(null)));
       // 测试 createTime 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setCreateTime(null)));
       // 准备参数
       OperationVideoPageReqVO reqVO = new OperationVideoPageReqVO();
       reqVO.setRoom(null);
       reqVO.setTitle(null);
       reqVO.setDoctor(null);
       reqVO.setPatient(null);
       reqVO.setOperationInfo(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       PageResult<OperationVideoDO> pageResult = operationVideoService.getOperationVideoPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbOperationVideo, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetOperationVideoList() {
       // mock 数据
       OperationVideoDO dbOperationVideo = randomPojo(OperationVideoDO.class, o -> { // 等会查询到
           o.setRoom(null);
           o.setTitle(null);
           o.setDoctor(null);
           o.setPatient(null);
           o.setOperationInfo(null);
           o.setCreateTime(null);
       });
       operationVideoMapper.insert(dbOperationVideo);
       // 测试 room 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setRoom(null)));
       // 测试 title 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setTitle(null)));
       // 测试 doctor 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setDoctor(null)));
       // 测试 patient 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setPatient(null)));
       // 测试 operationInfo 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setOperationInfo(null)));
       // 测试 createTime 不匹配
       operationVideoMapper.insert(ObjectUtils.clone(dbOperationVideo, o -> o.setCreateTime(null)));
       // 准备参数
       OperationVideoExportReqVO reqVO = new OperationVideoExportReqVO();
       reqVO.setRoom(null);
       reqVO.setTitle(null);
       reqVO.setDoctor(null);
       reqVO.setPatient(null);
       reqVO.setOperationInfo(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       List<OperationVideoDO> list = operationVideoService.getOperationVideoList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbOperationVideo, list.get(0));
    }

}
