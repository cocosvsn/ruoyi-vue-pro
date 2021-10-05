package cn.iocoder.yudao.adminserver.modules.dors.controller.version;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.constraints.*;
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

import cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version.VersionDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.version.VersionConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.version.VersionService;

@Api(tags = "应用版本")
@RestController
@RequestMapping("/dors/version")
@Validated
public class VersionController {

    @Resource
    private VersionService versionService;

    @PostMapping("/create")
    @ApiOperation("创建应用版本")
    @PreAuthorize("@ss.hasPermission('dors:version:create')")
    public CommonResult<Integer> create(@Valid @RequestBody VersionCreateReqVO createReqVO) {
        return success(versionService.create(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新应用版本")
    @PreAuthorize("@ss.hasPermission('dors:version:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody VersionUpdateReqVO updateReqVO) {
        versionService.update(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除应用版本")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:version:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Integer id) {
        versionService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得应用版本")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:version:query')")
    public CommonResult<VersionRespVO> get(@RequestParam("id") Integer id) {
        VersionDO versionDO = versionService.get(id);
        return success(VersionConvert.INSTANCE.convert(versionDO));
    }

    @GetMapping("/list")
    @ApiOperation("获得应用版本列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:version:query')")
    public CommonResult<List<VersionRespVO>> getList(@RequestParam("ids") Collection<Integer> ids) {
        List<VersionDO> list = versionService.getList(ids);
        return success(VersionConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得应用版本分页")
    @PreAuthorize("@ss.hasPermission('dors:version:query')")
    public CommonResult<PageResult<VersionRespVO>> getPage(@Valid VersionPageReqVO pageVO) {
        PageResult<VersionDO> pageResult = versionService.getPage(pageVO);
        return success(VersionConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出应用版本 Excel")
    @PreAuthorize("@ss.hasPermission('dors:version:export')")
    @OperateLog(type = EXPORT)
    public void exportExcel(@Valid VersionExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<VersionDO> list = versionService.getList(exportReqVO);
        // 导出 Excel
        List<VersionExcelVO> datas = VersionConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "应用版本.xls", "数据", VersionExcelVO.class, datas);
    }

    @GetMapping("/latest")
    @ApiOperation("获得应用最新版本")
    @ApiImplicitParam(name = "packageName", value = "包名", required = true, example = "com.akazs.dors", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermission('dors:version:query')")
    public CommonResult<VersionRespVO> latestVersion(@RequestParam("packageName") String packageName) {
        VersionDO versionDO = versionService.getLatestVersion(packageName);
        return success(VersionConvert.INSTANCE.convert(versionDO));
    }
}
