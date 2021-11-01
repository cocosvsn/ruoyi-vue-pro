package cn.iocoder.yudao.adminserver.modules.dors.controller.room;

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

import cn.iocoder.yudao.adminserver.modules.dors.controller.room.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.room.RoomDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.room.RoomConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.room.RoomService;

@Api(tags = "房间")
@RestController
@RequestMapping("/dors/room")
@Validated
public class RoomController {

    @Resource
    private RoomService roomService;

    @PostMapping("/create")
    @ApiOperation("创建房间")
    @PreAuthorize("@ss.hasPermission('dors:room:create')")
    public CommonResult<Integer> create(@Valid @RequestBody RoomCreateReqVO createReqVO) {
        return success(roomService.create(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新房间")
    @PreAuthorize("@ss.hasPermission('dors:room:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody RoomUpdateReqVO updateReqVO) {
        roomService.update(updateReqVO);
        return success(true);
    }

    @PutMapping("/bind-device")
    @ApiOperation("房间绑定设备")
    @PreAuthorize("@ss.hasPermission('dors:room:update')")
    public CommonResult<Boolean> bindDevice(@Valid @RequestBody RoomBindDeviceReqVO bindDeviceReqVO) {
        roomService.bindDevice(bindDeviceReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除房间")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:room:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Integer id) {
        roomService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得房间")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:room:query')")
    public CommonResult<RoomRespVO> get(@RequestParam("id") Integer id) {
        RoomDO roomDO = roomService.get(id);
        return success(RoomConvert.INSTANCE.convert(roomDO));
    }

    @GetMapping("/list")
    @ApiOperation("获得房间列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:room:query')")
    public CommonResult<List<RoomRespVO>> getList(@RequestParam("ids") Collection<Integer> ids) {
        List<RoomDO> list = roomService.getList(ids);
        return success(RoomConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得房间分页")
    @PreAuthorize("@ss.hasPermission('dors:room:query')")
    public CommonResult<PageResult<RoomRespVO>> getPage(@Valid RoomPageReqVO pageVO) {
        PageResult<RoomDO> pageResult = roomService.getPage(pageVO);
        return success(RoomConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出房间 Excel")
    @PreAuthorize("@ss.hasPermission('dors:room:export')")
    @OperateLog(type = EXPORT)
    public void exportExcel(@Valid RoomExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<RoomDO> list = roomService.getList(exportReqVO);
        // 导出 Excel
        List<RoomExcelVO> datas = RoomConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "房间.xls", "数据", RoomExcelVO.class, datas);
    }

    @GetMapping("/device")
    @ApiOperation("根据设备MAC地址查询房间及房间绑定的设备信息")
    @ApiImplicitParam(name = "mac", value = "设备MAC地址", required = true, example = "00:00:00:00:00:00", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermission('dors:room:query')")
    public CommonResult<RoomRespVO> getByDeviceMac(@RequestParam("mac") String mac) {
        RoomDO roomDO = roomService.getByMac(mac);
        return success(RoomConvert.INSTANCE.convert(roomDO));
    }
}
