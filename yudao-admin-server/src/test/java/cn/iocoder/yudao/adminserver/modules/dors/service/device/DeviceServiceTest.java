package cn.iocoder.yudao.adminserver.modules.dors.service.device;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.device.impl.DeviceServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.device.DeviceMapper;
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
* {@link DeviceServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(DeviceServiceImpl.class)
public class DeviceServiceTest extends BaseDbUnitTest {

    @Resource
    private DeviceServiceImpl Service;

    @Resource
    private DeviceMapper Mapper;

    @Test
    public void testCreate_success() {
        // 准备参数
        DeviceCreateReqVO reqVO = randomPojo(DeviceCreateReqVO.class);

        // 调用
        Integer Id = Service.create(reqVO);
        // 断言
        assertNotNull(Id);
        // 校验记录的属性是否正确
        DeviceDO deviceDO = Mapper.selectById(Id);
        assertPojoEquals(reqVO, deviceDO);
    }

    @Test
    public void testUpdate_success() {
        // mock 数据
        DeviceDO db = randomPojo(DeviceDO.class);
        Mapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        DeviceUpdateReqVO reqVO = randomPojo(DeviceUpdateReqVO.class, o -> {
            o.setId(db.getId()); // 设置更新的 ID
        });

        // 调用
        Service.update(reqVO);
        // 校验是否更新正确
        DeviceDO deviceDO = Mapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, deviceDO);
    }

    @Test
    public void testUpdate_notExists() {
        // 准备参数
        DeviceUpdateReqVO reqVO = randomPojo(DeviceUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> Service.update(reqVO), DEVICE_NOT_EXISTS);
    }

    @Test
    public void testDelete_success() {
        // mock 数据
        DeviceDO db = randomPojo(DeviceDO.class);
        Mapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = db.getId();

        // 调用
        Service.delete(id);
       // 校验数据不存在了
       assertNull(Mapper.selectById(id));
    }

    @Test
    public void testDelete_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> Service.delete(id), DEVICE_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetPage() {
       // mock 数据
       DeviceDO db = randomPojo(DeviceDO.class, o -> { // 等会查询到
           o.setRoom(null);
           o.setType(null);
           o.setSerialNo(null);
           o.setMac(null);
           o.setAppVersion(null);
           o.setSdkVersion(null);
           o.setSysVersion(null);
           o.setLastOnlineIp(null);
           o.setLastOnlineTime(null);
           o.setCreateTime(null);
           o.setRemarks(null);
       });
       Mapper.insert(db);
       // 测试 room 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setRoom(null)));
       // 测试 type 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
       // 测试 serialNo 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSerialNo(null)));
       // 测试 deviceMac 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setMac(null)));
       // 测试 appVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setAppVersion(null)));
       // 测试 sdkVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSdkVersion(null)));
       // 测试 sysVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSysVersion(null)));
       // 测试 lastOnlineIp 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setLastOnlineIp(null)));
       // 测试 lastOnlineTime 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setLastOnlineTime(null)));
       // 测试 createTime 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 测试 remarks 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setRemarks(null)));
       // 准备参数
       DevicePageReqVO reqVO = new DevicePageReqVO();
       reqVO.setRoom(null);
       reqVO.setType(null);
       reqVO.setSerialNo(null);
       reqVO.setMac(null);
       reqVO.setAppVersion(null);
       reqVO.setSdkVersion(null);
       reqVO.setSysVersion(null);
       reqVO.setLastOnlineIp(null);
       reqVO.setBeginLastOnlineTime(null);
       reqVO.setEndLastOnlineTime(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);
       reqVO.setRemarks(null);

       // 调用
       PageResult<DeviceDO> pageResult = Service.getPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(db, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetList() {
       // mock 数据
       DeviceDO db = randomPojo(DeviceDO.class, o -> { // 等会查询到
           o.setRoom(null);
           o.setType(null);
           o.setSerialNo(null);
           o.setMac(null);
           o.setAppVersion(null);
           o.setSdkVersion(null);
           o.setSysVersion(null);
           o.setLastOnlineIp(null);
           o.setLastOnlineTime(null);
           o.setCreateTime(null);
           o.setRemarks(null);
       });
       Mapper.insert(db);
       // 测试 room 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setRoom(null)));
       // 测试 type 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
       // 测试 serialNo 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSerialNo(null)));
       // 测试 deviceMac 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setMac(null)));
       // 测试 appVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setAppVersion(null)));
       // 测试 sdkVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSdkVersion(null)));
       // 测试 sysVersion 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setSysVersion(null)));
       // 测试 lastOnlineIp 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setLastOnlineIp(null)));
       // 测试 lastOnlineTime 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setLastOnlineTime(null)));
       // 测试 createTime 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 测试 remarks 不匹配
       Mapper.insert(ObjectUtils.clone(db, o -> o.setRemarks(null)));
       // 准备参数
       DeviceExportReqVO reqVO = new DeviceExportReqVO();
       reqVO.setRoom(null);
       reqVO.setType(null);
       reqVO.setSerialNo(null);
       reqVO.setMac(null);
       reqVO.setAppVersion(null);
       reqVO.setSdkVersion(null);
       reqVO.setSysVersion(null);
       reqVO.setLastOnlineIp(null);
       reqVO.setBeginLastOnlineTime(null);
       reqVO.setEndLastOnlineTime(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);
       reqVO.setRemarks(null);

       // 调用
       List<DeviceDO> list = Service.getList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(db, list.get(0));
    }

}
