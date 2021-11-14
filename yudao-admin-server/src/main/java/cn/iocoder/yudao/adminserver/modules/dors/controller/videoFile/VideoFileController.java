package cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile;

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

import cn.iocoder.yudao.adminserver.modules.dors.controller.videoFile.vo.*;
import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import cn.iocoder.yudao.adminserver.modules.dors.convert.videoFile.VideoFileConvert;
import cn.iocoder.yudao.adminserver.modules.dors.service.videoFile.VideoFileService;

@Api(tags = "手术视频文件")
@RestController
@RequestMapping("/dors/video-file")
@Validated
public class VideoFileController {

    @Resource
    private VideoFileService videoFileService;

    @PostMapping("/create")
    @ApiOperation("创建手术视频文件")
    @PreAuthorize("@ss.hasPermission('dors:video-file:create')")
    public CommonResult<Integer> createVideoFile(@Valid @RequestBody VideoFileCreateReqVO createReqVO) {
        return success(videoFileService.createVideoFile(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新手术视频文件")
    @PreAuthorize("@ss.hasPermission('dors:video-file:update')")
    public CommonResult<Boolean> updateVideoFile(@Valid @RequestBody VideoFileUpdateReqVO updateReqVO) {
        videoFileService.updateVideoFile(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除手术视频文件")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dors:video-file:delete')")
    public CommonResult<Boolean> deleteVideoFile(@RequestParam("id") Integer id) {
        videoFileService.deleteVideoFile(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得手术视频文件")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Integer.class)
    @PreAuthorize("@ss.hasPermission('dors:video-file:query')")
    public CommonResult<VideoFileRespVO> getVideoFile(@RequestParam("id") Integer id) {
        VideoFileDO videoFile = videoFileService.getVideoFile(id);
        return success(VideoFileConvert.INSTANCE.convert(videoFile));
    }

    @GetMapping("/list")
    @ApiOperation("获得手术视频文件列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('dors:video-file:query')")
    public CommonResult<List<VideoFileRespVO>> getVideoFileList(@RequestParam("ids") Collection<Integer> ids) {
        List<VideoFileDO> list = videoFileService.getVideoFileList(ids);
        return success(VideoFileConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得手术视频文件分页")
    @PreAuthorize("@ss.hasPermission('dors:video-file:query')")
    public CommonResult<PageResult<VideoFileRespVO>> getVideoFilePage(@Valid VideoFilePageReqVO pageVO) {
        PageResult<VideoFileDO> pageResult = videoFileService.getVideoFilePage(pageVO);
        return success(VideoFileConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出手术视频文件 Excel")
    @PreAuthorize("@ss.hasPermission('dors:video-file:export')")
    @OperateLog(type = EXPORT)
    public void exportVideoFileExcel(@Valid VideoFileExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<VideoFileDO> list = videoFileService.getVideoFileList(exportReqVO);
        // 导出 Excel
        List<VideoFileExcelVO> datas = VideoFileConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "手术视频文件.xls", "数据", VideoFileExcelVO.class, datas);
    }

}
