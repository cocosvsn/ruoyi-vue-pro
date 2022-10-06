package cn.iocoder.yudao.adminserver.modules.dors.service.live;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.live.impl.LiveServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.live.LiveMapper;
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
* {@link LiveServiceImpl} 的单元测试类
*
* @author bunco
*/
@Import(LiveServiceImpl.class)
public class LiveServiceTest extends BaseDbUnitTest {

    @Resource
    private LiveServiceImpl liveService;

    @Resource
    private LiveMapper liveMapper;

    @Test
    public void testCreateLive_success() {
        // 准备参数
        LiveCreateReqVO reqVO = randomPojo(LiveCreateReqVO.class);

        // 调用
        Integer liveId = liveService.createLive(reqVO);
        // 断言
        assertNotNull(liveId);
        // 校验记录的属性是否正确
        LiveDO live = liveMapper.selectById(liveId);
        assertPojoEquals(reqVO, live);
    }

    @Test
    public void testUpdateLive_success() {
        // mock 数据
        LiveDO dbLive = randomPojo(LiveDO.class);
        liveMapper.insert(dbLive);// @Sql: 先插入出一条存在的数据
        // 准备参数
        LiveUpdateReqVO reqVO = randomPojo(LiveUpdateReqVO.class, o -> {
            o.setId(dbLive.getId()); // 设置更新的 ID
        });

        // 调用
        liveService.updateLive(reqVO);
        // 校验是否更新正确
        LiveDO live = liveMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, live);
    }

    @Test
    public void testUpdateLive_notExists() {
        // 准备参数
        LiveUpdateReqVO reqVO = randomPojo(LiveUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> liveService.updateLive(reqVO), LIVE_NOT_EXISTS);
    }

    @Test
    public void testDeleteLive_success() {
        // mock 数据
        LiveDO dbLive = randomPojo(LiveDO.class);
        liveMapper.insert(dbLive);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbLive.getId();

        // 调用
        liveService.deleteLive(id);
       // 校验数据不存在了
       assertNull(liveMapper.selectById(id));
    }

    @Test
    public void testDeleteLive_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> liveService.deleteLive(id), LIVE_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetLivePage() {
       // mock 数据
       LiveDO dbLive = randomPojo(LiveDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setCreateTime(null);
       });
       liveMapper.insert(dbLive);
       // 测试 name 不匹配
       liveMapper.insert(ObjectUtils.clone(dbLive, o -> o.setName(null)));
       // 测试 createTime 不匹配
       liveMapper.insert(ObjectUtils.clone(dbLive, o -> o.setCreateTime(null)));
       // 准备参数
       LivePageReqVO reqVO = new LivePageReqVO();
       reqVO.setName(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       PageResult<LiveDO> pageResult = liveService.getLivePage(reqVO, null);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbLive, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetLiveList() {
       // mock 数据
       LiveDO dbLive = randomPojo(LiveDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setCreateTime(null);
       });
       liveMapper.insert(dbLive);
       // 测试 name 不匹配
       liveMapper.insert(ObjectUtils.clone(dbLive, o -> o.setName(null)));
       // 测试 createTime 不匹配
       liveMapper.insert(ObjectUtils.clone(dbLive, o -> o.setCreateTime(null)));
       // 准备参数
       LiveExportReqVO reqVO = new LiveExportReqVO();
       reqVO.setName(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       List<LiveDO> list = liveService.getLiveList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbLive, list.get(0));
    }

}
