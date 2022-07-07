package cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo;

import cn.iocoder.yudao.adminserver.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.yudao.adminserver.modules.infra.service.config.InfConfigService;
import cn.iocoder.yudao.framework.file.config.FileProperties;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.*;
import javax.servlet.http.*;
import java.text.MessageFormat;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import static cn.iocoder.yudao.adminserver.modules.infra.enums.InfDictTypeConstants.SERVER_IP;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.adminserver.modules.dors.controller.operationVideo.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo.OperationVideoDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.operationVideo.OperationVideoConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.operationVideo.OperationVideoService;

@Api(tags = "手术视频")
@RestController
@RequestMapping("/dors/operation-video")
@Validated
public class OperationVideoController {

    @Resource
    private FileProperties fileProperties;
    @Resource
    private InfConfigService infConfigService;
    @Resource
    private OperationVideoService operationVideoService;

    @PostMapping("/create")
    @ApiOperation("创建手术视频")
    @PreAuthorize("@ss.hasPermission('dors:operation-video:create')")
    public CommonResult<Integer> createOperationVideo(@Valid @RequestBody OperationVideoCreateReqVO createReqVO) {
        return success(operationVideoService.createOperationVideo(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新手术视频")
    @PreAuthorize("@ss.hasPermission('dors:operation-video:update')")
    public CommonResult<Boolean> updateOperationVideo(@Valid @RequestBody OperationVideoUpdateReqVO updateReqVO) {
        operationVideoService.updateOperationVideo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除手术视频")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:operation-video:delete')")
    public CommonResult<Boolean> deleteOperationVideo(@RequestParam("id") Integer id) {
        operationVideoService.deleteOperationVideo(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得手术视频")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:operation-video:query')")
    public CommonResult<OperationVideoRespVO> getOperationVideo(@RequestParam("id") Integer id) {
        OperationVideoDO operationVideo = operationVideoService.getOperationVideo(id);
        return success(OperationVideoConvert.INSTANCE.convert(operationVideo));
    }

    @GetMapping("/list")
    @ApiOperation("获得手术视频列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:operation-video:query')")
    public CommonResult<List<OperationVideoRespVO>> getOperationVideoList(@RequestParam("ids") Collection<Integer> ids) {
        List<OperationVideoDO> list = operationVideoService.getOperationVideoList(ids);
        return success(OperationVideoConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得手术视频分页")
    @PreAuthorize("@ss.hasPermission('dors:operation-video:query')")
    public CommonResult<PageResult<OperationVideoRespVO>> getOperationVideoPage(@Valid OperationVideoPageReqVO pageVO) {
        PageResult<OperationVideoDO> pageResult = operationVideoService.getOperationVideoPage(pageVO);
        InfConfigDO infConfigDO = this.infConfigService.getConfigByKey(SERVER_IP);
        CommonResult result = success(OperationVideoConvert.INSTANCE.convertPage(pageResult));
        if (null != infConfigDO) {
            result.setMsg(MessageFormat.format(fileProperties.getUrlPrefix(), infConfigDO.getValue())); // 放入msg中，以便前端拼接出完整访问路径。
        }
        return result;
    }

    @GetMapping("/room")
    @ApiOperation("手术室获得手术视频分页")
    public CommonResult<PageResult<OperationVideoRespVO>> getOperationVideoByRoom(@Valid OperationVideoPageReqVO pageVO) {
        return getOperationVideoPage(pageVO);
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出手术视频 Excel")
    @PreAuthorize("@ss.hasPermission('dors:operation-video:export')")
    @OperateLog(type = EXPORT)
    public void exportOperationVideoExcel(@Valid OperationVideoExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<OperationVideoDO> list = operationVideoService.getOperationVideoList(exportReqVO);
        // 导出 Excel
        List<OperationVideoExcelVO> datas = OperationVideoConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "手术视频.xls", "数据", OperationVideoExcelVO.class, datas);
    }

}
