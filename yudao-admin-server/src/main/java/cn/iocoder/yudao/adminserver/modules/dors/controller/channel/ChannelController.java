package cn.iocoder.yudao.adminserver.modules.dors.controller.channel;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.adminserver.modules.dors.controller.channel.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.channel.ChannelDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.channel.ChannelConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.channel.ChannelService;

@Api(tags = "频道")
@RestController
@RequestMapping("/dors/channel")
@Validated
public class ChannelController {

    @Resource
    private ChannelService channelService;

    @PostMapping("/create")
    @ApiOperation("创建频道")
    @PreAuthorize("@ss.hasPermission('dors:channel:create')")
    public CommonResult<Integer> create(@Valid @RequestBody ChannelCreateReqVO createReqVO) {
        return success(channelService.create(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新频道")
    @PreAuthorize("@ss.hasPermission('dors:channel:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ChannelUpdateReqVO updateReqVO) {
        channelService.update(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除频道")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:channel:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Integer id) {
        channelService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得频道")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:channel:query')")
    public CommonResult<ChannelRespVO> get(@RequestParam("id") Integer id) {
        ChannelDO channelDO = channelService.get(id);
        return success(ChannelConvert.INSTANCE.convert(channelDO));
    }

    @GetMapping("/list")
    @ApiOperation("获得频道列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:channel:query')")
    public CommonResult<List<ChannelRespVO>> getList(@RequestParam("ids") Collection<Integer> ids) {
        List<ChannelDO> list = channelService.getList(ids);
        return success(ChannelConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得频道分页")
    @PreAuthorize("@ss.hasPermission('dors:channel:query')")
    public CommonResult<PageResult<ChannelRespVO>> getPage(@Valid ChannelPageReqVO pageVO) {
        PageResult<ChannelDO> pageResult = channelService.getPage(pageVO);
        return success(ChannelConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出频道 Excel")
    @PreAuthorize("@ss.hasPermission('dors:channel:export')")
    @OperateLog(type = EXPORT)
    public void exportExcel(@Valid ChannelExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ChannelDO> list = channelService.getList(exportReqVO);
        // 导出 Excel
        List<ChannelExcelVO> datas = ChannelConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "频道.xls", "数据", ChannelExcelVO.class, datas);
    }

    @GetMapping("")
    @ApiOperation("获得频道列表")
//    @PreAuthorize("@ss.hasPermission('dors:channel:query')")
    public CommonResult<List<ChannelRespVO>> getList(@Valid ChannelPageReqVO pageVO) {
        List<ChannelDO> result = channelService.getList(pageVO);
        return success(ChannelConvert.INSTANCE.convertList(result));
    }

    @GetMapping("/device")
    @ApiOperation("获得设备指定类型频道列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备编号", required = true, example = "1024", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "type", value = "频道类型", required = true, example = "vi/usb/net/ndi/file/mix", dataTypeClass = String.class)
    })
    //    @PreAuthorize("@ss.hasPermission('dors:channel:query')")
    public CommonResult<List<ChannelRespVO>> getByDeviceAndType(@RequestParam("deviceId") Integer deviceId, @RequestParam("type") String typeOf) {
        List<ChannelDO> channelDOs = channelService.getByDeviceAndType(deviceId, typeOf);
        return success(ChannelConvert.INSTANCE.convertList(channelDOs));
    }
}
