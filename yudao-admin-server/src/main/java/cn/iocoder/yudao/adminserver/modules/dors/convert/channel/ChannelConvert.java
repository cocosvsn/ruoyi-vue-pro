package cn.iocoder.yudao.adminserver.modules.dors.convert.channel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;

/**
 * 频道 Convert
 *
 * @author Bunco
 */
@Mapper
public interface ChannelConvert {

    ChannelConvert INSTANCE = Mappers.getMapper(ChannelConvert.class);

    ChannelDO convert(ChannelCreateReqVO bean);

    ChannelDO convert(ChannelUpdateReqVO bean);

    ChannelRespVO convert(ChannelDO bean);

    List<ChannelRespVO> convertList(List<ChannelDO> list);

    PageResult<ChannelRespVO> convertPage(PageResult<ChannelDO> page);

    List<ChannelExcelVO> convertList02(List<ChannelDO> list);

}
