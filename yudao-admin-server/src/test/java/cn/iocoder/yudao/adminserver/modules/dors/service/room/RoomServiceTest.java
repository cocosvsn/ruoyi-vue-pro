package cn.iocoder.yudao.adminserver.modules.dors.service.room;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.impl.RoomServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.room.RoomMapper;
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
* {@link RoomServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(RoomServiceImpl.class)
public class RoomServiceTest extends BaseDbUnitTest {

    @Resource
    private RoomServiceImpl roomService;

    @Resource
    private RoomMapper roomMapper;

    @Test
    public void testCreate_success() {
        // 准备参数
        RoomCreateReqVO reqVO = randomPojo(RoomCreateReqVO.class);

        // 调用
        Integer Id = roomService.create(reqVO);
        // 断言
        assertNotNull(Id);
        // 校验记录的属性是否正确
        RoomDO roomDO = roomMapper.selectById(Id);
        assertPojoEquals(reqVO, roomDO);
    }

    @Test
    public void testUpdate_success() {
        // mock 数据
        RoomDO db = randomPojo(RoomDO.class);
        roomMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        RoomUpdateReqVO reqVO = randomPojo(RoomUpdateReqVO.class, o -> {
            o.setId(db.getId()); // 设置更新的 ID
        });

        // 调用
        roomService.update(reqVO);
        // 校验是否更新正确
        RoomDO roomDO = roomMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, roomDO);
    }

    @Test
    public void testUpdate_notExists() {
        // 准备参数
        RoomUpdateReqVO reqVO = randomPojo(RoomUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> roomService.update(reqVO), ROOM_NOT_EXISTS);
    }

    @Test
    public void testDelete_success() {
        // mock 数据
        RoomDO db = randomPojo(RoomDO.class);
        roomMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = db.getId();

        // 调用
        roomService.delete(id);
        // 校验数据不存在了
        assertNull(roomMapper.selectById(id));
    }

    @Test
    public void testDelete_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> roomService.delete(id), ROOM_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetPage() {
       // mock 数据
       RoomDO db = randomPojo(RoomDO.class, o -> { // 等会查询到
           o.setType(null);
           o.setName(null);
           o.setCreateTime(null);
           o.setRemarks(null);
       });
        roomMapper.insert(db);
       // 测试 type 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
       // 测试 name 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
       // 测试 createTime 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 测试 remarks 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setRemarks(null)));
       // 准备参数
       RoomPageReqVO reqVO = new RoomPageReqVO();
       reqVO.setType(null);
       reqVO.setName(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);
       reqVO.setRemarks(null);

       // 调用
       PageResult<RoomDO> pageResult = roomService.getPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(db, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetList() {
       // mock 数据
       RoomDO db = randomPojo(RoomDO.class, o -> { // 等会查询到
           o.setType(null);
           o.setName(null);
           o.setCreateTime(null);
           o.setRemarks(null);
       });
        roomMapper.insert(db);
       // 测试 type 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
       // 测试 name 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
       // 测试 createTime 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 测试 remarks 不匹配
        roomMapper.insert(ObjectUtils.clone(db, o -> o.setRemarks(null)));
       // 准备参数
       RoomExportReqVO reqVO = new RoomExportReqVO();
       reqVO.setType(null);
       reqVO.setName(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);
       reqVO.setRemarks(null);

       // 调用
       List<RoomDO> list = roomService.getList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(db, list.get(0));
    }

}
