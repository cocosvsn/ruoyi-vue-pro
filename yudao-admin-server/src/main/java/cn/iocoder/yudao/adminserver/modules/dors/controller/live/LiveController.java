package cn.iocoder.yudao.adminserver.modules.dors.controller.live;

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

import cn.iocoder.yudao.adminserver.modules.dors.controller.live.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live.LiveDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.live.LiveConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.live.LiveService;

@Api(tags = "直播")
@RestController
@RequestMapping("/dors/live")
@Validated
public class LiveController {

    @Resource
    private LiveService liveService;

    @PostMapping("/create")
    @ApiOperation("创建直播")
    @PreAuthorize("@ss.hasPermission('dors:live:create')")
    public CommonResult<Integer> createLive(@Valid @RequestBody LiveCreateReqVO createReqVO) {
        return success(liveService.createLive(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新直播")
    @PreAuthorize("@ss.hasPermission('dors:live:update')")
    public CommonResult<Boolean> updateLive(@Valid @RequestBody LiveUpdateReqVO updateReqVO) {
        liveService.updateLive(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除直播")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:live:delete')")
    public CommonResult<Boolean> deleteLive(@RequestParam("id") Integer id) {
        liveService.deleteLive(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得直播")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:live:query')")
    public CommonResult<LiveRespVO> getLive(@RequestParam("id") Integer id) {
        LiveDO live = liveService.getLive(id);
        return success(LiveConvert.INSTANCE.convert(live));
    }

    @GetMapping("/list")
    @ApiOperation("获得直播列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:live:query')")
    public CommonResult<List<LiveRespVO>> getLiveList(@RequestParam("ids") Collection<Integer> ids) {
        List<LiveDO> list = liveService.getLiveList(ids);
        return success(LiveConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得直播分页")
    @PreAuthorize("@ss.hasPermission('dors:live:query')")
    public CommonResult<PageResult<LiveRespVO>> getLivePage(@Valid LivePageReqVO pageVO) {
        PageResult<LiveDO> pageResult = liveService.getLivePage(pageVO);
        return success(LiveConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出直播 Excel")
    @PreAuthorize("@ss.hasPermission('dors:live:export')")
    @OperateLog(type = EXPORT)
    public void exportLiveExcel(@Valid LiveExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<LiveDO> list = liveService.getLiveList(exportReqVO);
        // 导出 Excel
        List<LiveExcelVO> datas = LiveConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "直播.xls", "数据", LiveExcelVO.class, datas);
    }

}
