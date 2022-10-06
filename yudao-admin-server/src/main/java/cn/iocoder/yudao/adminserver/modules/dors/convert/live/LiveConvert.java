package cn.iocoder.yudao.adminserver.modules.dors.convert.live;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;

/**
 * 直播 Convert
 *
 * @author bunco
 */
@Mapper
public interface LiveConvert {

    LiveConvert INSTANCE = Mappers.getMapper(LiveConvert.class);

    LiveDO convert(LiveCreateReqVO bean);

    LiveDO convert(LiveUpdateReqVO bean);

    LiveRespVO convert(LiveDO bean);

    List<LiveRespVO> convertList(List<LiveDO> list);

    PageResult<LiveRespVO> convertPage(PageResult<LiveDO> page);

    List<LiveExcelVO> convertList02(List<LiveDO> list);

}
