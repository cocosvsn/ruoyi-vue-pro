package cn.iocoder.yudao.adminserver.modules.dors.service.device;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备 Service 接口
 *
 * @author Bunco
 */
public interface DeviceService {

    /**
     * 创建设备
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer create(@Valid DeviceCreateReqVO createReqVO);

    /**
     * 更新设备
     *
     * @param updateReqVO 更新信息
     */
    void update(@Valid DeviceUpdateReqVO updateReqVO);

    /**
     * 删除设备
     *
     * @param id 编号
     */
    void delete(Integer id);

    /**
     * 获得设备
     *
     * @param id 编号
     * @return 设备
     */
    DeviceDO get(Integer id);

    /**
     * 获得设备列表
     *
     * @param ids 编号
     * @return 设备列表
     */
    List<DeviceDO> getList(Collection<Integer> ids);

    /**
     * 获得设备分页
     *
     * @param pageReqVO 分页查询
     * @return 设备分页
     */
    PageResult<DeviceDO> getPage(DevicePageReqVO pageReqVO);

    /**
     * 获得设备列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备列表
     */
    List<DeviceDO> getList(DeviceExportReqVO exportReqVO);

    /**
     * 重新扫描设备
     */
    void rediscover();

    /**
     * 获取灵派编码器配置
     * @param ip
     */
    String getConfigLinkPi(String ip);

    /**
     * 灵派编码器配置
     * @param ip
     * @param config
     */
    String configLinkPi(String ip, String config);

    /**
     * 获取示见编码器配置
     * @param ip
     */
    String getConfigShxit(String ip);

    /**
     * 示见编码器配置
     * @param config
     */
    String configShxit(String ip, String config);

    /**
     * 示见编码器画面布局配置
     * @param layout 画面布局
     * @param layoutParams 画面布局参数
     */
    String configShxitLayout(String ip, String layout, String layoutParams);
}
