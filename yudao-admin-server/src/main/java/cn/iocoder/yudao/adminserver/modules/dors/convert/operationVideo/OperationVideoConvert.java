package cn.iocoder.yudao.adminserver.modules.dors.convert.operationVideo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;

/**
 * 手术视频 Convert
 *
 * @author Bunco
 */
@Mapper
public interface OperationVideoConvert {

    OperationVideoConvert INSTANCE = Mappers.getMapper(OperationVideoConvert.class);

    OperationVideoDO convert(OperationVideoCreateReqVO bean);

    OperationVideoDO convert(OperationVideoUpdateReqVO bean);

    OperationVideoRespVO convert(OperationVideoDO bean);

    List<OperationVideoRespVO> convertList(List<OperationVideoDO> list);

    PageResult<OperationVideoRespVO> convertPage(PageResult<OperationVideoDO> page);

    List<OperationVideoExcelVO> convertList02(List<OperationVideoDO> list);

}
