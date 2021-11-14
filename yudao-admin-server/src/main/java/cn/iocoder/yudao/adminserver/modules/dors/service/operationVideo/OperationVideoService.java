package cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 手术视频 Service 接口
 *
 * @author Bunco
 */
public interface OperationVideoService {

    /**
     * 创建手术视频
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createOperationVideo(@Valid OperationVideoCreateReqVO createReqVO);

    /**
     * 更新手术视频
     *
     * @param updateReqVO 更新信息
     */
    void updateOperationVideo(@Valid OperationVideoUpdateReqVO updateReqVO);

    /**
     * 删除手术视频
     *
     * @param id 编号
     */
    void deleteOperationVideo(Integer id);

    /**
     * 获得手术视频
     *
     * @param id 编号
     * @return 手术视频
     */
    OperationVideoDO getOperationVideo(Integer id);

    /**
     * 获得手术视频列表
     *
     * @param ids 编号
     * @return 手术视频列表
     */
    List<OperationVideoDO> getOperationVideoList(Collection<Integer> ids);

    /**
     * 获得手术视频分页
     *
     * @param pageReqVO 分页查询
     * @return 手术视频分页
     */
    PageResult<OperationVideoDO> getOperationVideoPage(OperationVideoPageReqVO pageReqVO);

    /**
     * 获得手术视频列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 手术视频列表
     */
    List<OperationVideoDO> getOperationVideoList(OperationVideoExportReqVO exportReqVO);

}
