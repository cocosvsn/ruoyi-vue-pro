package cn.iocoder.yudao.adminserver.modules.dors.service.channel;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import cn.iocoder.yudao.adminserver.BaseDbUnitTest;
import cn.iocoder.yudao.adminserver.modules.dors.service.channel.impl.ChannelServiceImpl;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.dal.mysql.channel.ChannelMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;
import java.util.*;

import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.CHANNEL_NOT_EXISTS;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomInteger;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.*;

/**
* {@link ChannelServiceImpl} 的单元测试类
*
* @author Bunco
*/
@Import(ChannelServiceImpl.class)
public class ChannelServiceTest extends BaseDbUnitTest {

    @Resource
    private ChannelServiceImpl channelService;

    @Resource
    private ChannelMapper channelMapper;

    @Test
    public void testCreate_success() {
        // 准备参数
        ChannelCreateReqVO reqVO = randomPojo(ChannelCreateReqVO.class);

        // 调用
        Integer Id = channelService.create(reqVO);
        // 断言
        assertNotNull(Id);
        // 校验记录的属性是否正确
        ChannelDO channelDO = channelMapper.selectById(Id);
        assertPojoEquals(reqVO, channelDO);
    }

    @Test
    public void testUpdate_success() {
        // mock 数据
        ChannelDO db = randomPojo(ChannelDO.class);
        channelMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ChannelUpdateReqVO reqVO = randomPojo(ChannelUpdateReqVO.class, o -> {
            o.setId(db.getId()); // 设置更新的 ID
        });

        // 调用
        channelService.update(reqVO);
        // 校验是否更新正确
        ChannelDO channelDO = channelMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, channelDO);
    }

    @Test
    public void testUpdate_notExists() {
        // 准备参数
        ChannelUpdateReqVO reqVO = randomPojo(ChannelUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> channelService.update(reqVO), CHANNEL_NOT_EXISTS);
    }

    @Test
    public void testDelete_success() {
        // mock 数据
        ChannelDO db = randomPojo(ChannelDO.class);
        channelMapper.insert(db);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = db.getId();

        // 调用
        channelService.delete(id);
        // 校验数据不存在了
        assertNull(channelMapper.selectById(id));
    }

    @Test
    public void testDelete_notExists() {
        // 准备参数
        Integer id = randomInteger();

        // 调用, 并断言异常
        assertServiceException(() -> channelService.delete(id), CHANNEL_NOT_EXISTS);
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetPage() {
       // mock 数据
       ChannelDO db = randomPojo(ChannelDO.class, o -> { // 等会查询到
           o.setRoom(null);
           o.setType(null);
           o.setName(null);
           o.setCreateTime(null);
       });
        channelMapper.insert(db);
       // 测试 device 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setRoom(null)));
       // 测试 type 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
       // 测试 name 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
       // 测试 createTime 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
       // 准备参数
       ChannelPageReqVO reqVO = new ChannelPageReqVO();
       reqVO.setRoom(null);
       reqVO.setType(null);
       reqVO.setName(null);
       reqVO.setBeginCreateTime(null);
       reqVO.setEndCreateTime(null);

       // 调用
       PageResult<ChannelDO> pageResult = channelService.getPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(db, pageResult.getList().get(0));
    }

    @Test // TODO 请修改 null 为需要的值
    public void testGetList() {
        // mock 数据
        ChannelDO db = randomPojo(ChannelDO.class, o -> { // 等会查询到
            o.setRoom(null);
            o.setType(null);
            o.setName(null);
            o.setCreateTime(null);
        });
        channelMapper.insert(db);
        // 测试 device 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setRoom(null)));
        // 测试 type 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setType(null)));
        // 测试 name 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setName(null)));
        // 测试 createTime 不匹配
        channelMapper.insert(ObjectUtils.clone(db, o -> o.setCreateTime(null)));
        // 准备参数
        ChannelExportReqVO reqVO = new ChannelExportReqVO();
        reqVO.setRoom(null);
        reqVO.setType(null);
        reqVO.setName(null);
        reqVO.setBeginCreateTime(null);
        reqVO.setEndCreateTime(null);

        // 调用
        List<ChannelDO> list = channelService.getList(reqVO);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(db, list.get(0));
    }
}
