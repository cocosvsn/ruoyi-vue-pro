package cn.iocoder.yudao.adminserver.modules.dors.controller.device;

import cn.iocoder.yudao.adminserver.modules.dors.enums.DeviceType;
import cn.iocoder.yudao.adminserver.modules.dors.enums.EncoderType;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.file.config.FileProperties;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.io.File;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import static cn.iocoder.yudao.adminserver.modules.dors.enums.DorsErrorCodeConstants.DEVICE_ENCODER_ERROR;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.adminserver.modules.dors.controller.device.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.device.DeviceDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.device.DeviceConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.device.DeviceService;

@Api(tags = "设备")
@RestController
@RequestMapping("/dors/device")
@Validated
public class DeviceController {

    @Resource
    private DeviceService deviceService;
    @Resource
    private FileProperties fileProperties;

    @PostMapping("/create")
    @ApiOperation("创建设备")
    @PreAuthorize("@ss.hasPermission('dors:device:create')")
    public CommonResult<Integer> create(@Valid @RequestBody DeviceCreateReqVO createReqVO) {
        return success(deviceService.create(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新设备")
    @PreAuthorize("@ss.hasPermission('dors:device:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody DeviceUpdateReqVO updateReqVO) {
        deviceService.update(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除设备")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:device:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Integer id) {
        deviceService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得设备")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:device:query')")
    public CommonResult<DeviceRespVO> get(@RequestParam("id") Integer id) {
        DeviceDO deviceDO = deviceService.get(id);
        return success(DeviceConvert.INSTANCE.convert(deviceDO));
    }

    @GetMapping("/list")
    @ApiOperation("获得设备列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:device:query')")
    public CommonResult<List<DeviceRespVO>> getList(@RequestParam("ids") Collection<Integer> ids) {
        List<DeviceDO> list = deviceService.getList(ids);
        return success(DeviceConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得设备分页")
    @PreAuthorize("@ss.hasPermission('dors:device:query')")
    public CommonResult<PageResult<DeviceRespVO>> getPage(@Valid DevicePageReqVO pageVO) {
        PageResult<DeviceDO> pageResult = deviceService.getPage(pageVO);
        return success(DeviceConvert.INSTANCE.convertPage(pageResult));
    }

    @PutMapping("/rediscover")
    @ApiOperation("重新搜索并获取设备信息")
    @PreAuthorize("@ss.hasPermission('dors:device:update')")
    public CommonResult<DeviceRespVO> rediscover() {
        this.deviceService.rediscover();
        return success(null);
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出设备 Excel")
    @PreAuthorize("@ss.hasPermission('dors:device:export')")
    @OperateLog(type = EXPORT)
    public void exportExcel(@Valid DeviceExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<DeviceDO> list = deviceService.getList(exportReqVO);
        // 导出 Excel
        List<DeviceExcelVO> datas = DeviceConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "设备.xls", "数据", DeviceExcelVO.class, datas);
    }

    @GetMapping("/usage")
    @ApiOperation("获得服务器磁盘使用情况")
    @PreAuthorize("@ss.hasPermission('dors:device:query')")
    public CommonResult<List<DeviceSpaseVO>> getDiskUsage() {
        String dir = fileProperties.getLocal().getDirectory();
        File file = new File(dir);
        List<DeviceSpaseVO> list = new ArrayList<>();
        long totalSpace = file.getTotalSpace();
        long usableSpace = file.getUsableSpace();
        DeviceSpaseVO usedSpaceVo = new DeviceSpaseVO();
        usedSpaceVo.setName("已用空间");
        usedSpaceVo.setValue(totalSpace - usableSpace);
        list.add(usedSpaceVo);

        DeviceSpaseVO usableSpaceVo = new DeviceSpaseVO();
        usableSpaceVo.setName("可用空间");
        usableSpaceVo.setValue(usableSpace);
        list.add(usableSpaceVo);
        return success(list);
    }

    @GetMapping("/get-device-config")
    @ApiOperation("获取编解码器配置")
    @PreAuthorize("@ss.hasPermission('dors:device:query')")
    public CommonResult<String> getDeviceConfig(@RequestParam("ip") String ip,
                                                 @RequestParam("type") EncoderType encoderType) {
        switch (encoderType) {
            case LINKPI:
                return success(this.deviceService.getConfigLinkPi(ip));
            case SHXIT:
                return success(this.deviceService.getConfigShxit(ip));
            default:
                return error(DEVICE_ENCODER_ERROR);
        }
    }

    @PostMapping("/config-device")
    @ApiOperation("配置编解码器")
    @PreAuthorize("@ss.hasPermission('dors:device:update')")
    public CommonResult<String> configDeviceConfig(@RequestParam("ip") String ip,
                                                   @RequestParam("type") EncoderType encoderType,
                                                   @RequestBody String config) {
        switch (encoderType) {
            case LINKPI:
                return success(this.deviceService.configLinkPi(ip, config));
            case SHXIT:
                return success(this.deviceService.configShxit(ip, config));
            default:
                return error(DEVICE_ENCODER_ERROR);
        }
    }
}
