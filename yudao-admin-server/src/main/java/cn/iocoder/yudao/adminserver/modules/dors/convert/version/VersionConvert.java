package cn.iocoder.yudao.adminserver.modules.dors.convert.version;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;

/**
 * 应用版本 Convert
 *
 * @author Bunco
 */
@Mapper
public interface VersionConvert {

    VersionConvert INSTANCE = Mappers.getMapper(VersionConvert.class);

    VersionDO convert(VersionCreateReqVO bean);

    VersionDO convert(VersionUpdateReqVO bean);

    VersionRespVO convert(VersionDO bean);

    List<VersionRespVO> convertList(List<VersionDO> list);

    PageResult<VersionRespVO> convertPage(PageResult<VersionDO> page);

    List<VersionExcelVO> convertList02(List<VersionDO> list);

}
