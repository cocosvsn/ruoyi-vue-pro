package cn.iocoder.yudao.adminserver.modules.dors.service.version;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.version.impl.VersionServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.version.VersionMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;
import java.util.*;

import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.VERSION_NOT_EXISTS;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomInteger;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.*;

/**
* {@link VersionServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(VersionServiceImpl.class)
public class VersionServiceTest extends BaseDbUnitTest {

    @Resource
    private VersionServiceImpl versionService;

    @Resource
    private VersionMapper versionMapper;

    @Test
    public void testCreate_success() {
        // 准备参数
        VersionCreateReqVO reqVO = randomPojo(VersionCreateReqVO.class);

        // 调用
        Integer Id = versionService.create(reqVO);
        // 断言
        assertNotNull(Id);
        // 校验记录的属性是否正确
        VersionDO versionDO = versionMapper.selectById(Id);
        assertPojoEquals(reqVO, versionDO);
    }

    @Test
    public void testUpdate_success() {
        // mock 数据
        VersionDO db = randomPojo(VersionDO.class);
        versionMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        VersionUpdateReqVO reqVO = randomPojo(VersionUpdateReqVO.class, o -> {
            o.setId(db.getId()); // 设置更新的 ID
        });

        // 调用
        versionService.update(reqVO);
        // 校验是否更新正确
        VersionDO versionDO = versionMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, versionDO);
    }

    @Test
    public void testUpdate_notExists() {
        // 准备参数
        VersionUpdateReqVO reqVO = randomPojo(VersionUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> versionService.update(reqVO), VERSION_NOT_EXISTS);
    }

    @Test
    public void testDelete_success() {
        // mock 数据
        VersionDO db = randomPojo(VersionDO.class);
        versionMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = db.getId();

        // 调用
        versionService.delete(id);
       // 校验数据不存在了
       assertNull(versionMapper.selectById(id));
    }

    @Test
    public void testDelete_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> versionService.delete(id), VERSION_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetPage() {
       // mock 数据
       VersionDO db = randomPojo(VersionDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setPackageName(null);
           o.setVersionNum(null);
           o.setVersionCode(null);
           o.setCreateTime(null);
       });
        versionMapper.insert(db);
       // 测试 name 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
       // 测试 packageName 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setPackageName(null)));
       // 测试 versionNum 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setVersionNum(null)));
       // 测试 versionCode 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setVersionCode(null)));
       // 测试 createTime 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 准备参数
       VersionPageReqVO reqVO = new VersionPageReqVO();
       reqVO.setName(null);
       reqVO.setPackageName(null);
       reqVO.setVersionNum(null);
       reqVO.setVersionCode(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       PageResult<VersionDO> pageResult = versionService.getPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(db, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetList() {
       // mock 数据
       VersionDO db = randomPojo(VersionDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setPackageName(null);
           o.setVersionNum(null);
           o.setVersionCode(null);
           o.setCreateTime(null);
       });
        versionMapper.insert(db);
       // 测试 name 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
       // 测试 packageName 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setPackageName(null)));
       // 测试 versionNum 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setVersionNum(null)));
       // 测试 versionCode 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setVersionCode(null)));
       // 测试 createTime 不匹配
        versionMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 准备参数
       VersionExportReqVO reqVO = new VersionExportReqVO();
       reqVO.setName(null);
       reqVO.setPackageName(null);
       reqVO.setVersionNum(null);
       reqVO.setVersionCode(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       List<VersionDO> list = versionService.getList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(db, list.get(0));
    }

}
