package cn.iocoder.yudao.adminserver.modules.dors.controller.version;

import cn.iocoder.yudao.framework.file.config.FileProperties;
import cn.iocoder.yudao.framework.file.core.handler.FileHandler;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.Icon;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

@Slf4j
@Api(tags = "应用版本")
@RestController
@RequestMapping("/dors/version")
@Validated
public class VersionController {

    @Resource
    private FileHandler fileHandler;
    @Resource
    private FileProperties fileProperties;
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

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件附件", required = true, dataTypeClass = MultipartFile.class)
    })
    public CommonResult<VersionRespVO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        VersionDO versionDO = null;
        // 新命名
        String fileId = createFileId();
        String newName = rename(fileId, file.getOriginalFilename());
        String relativePath = createRelativeDirectory() + newName;
        this.fileHandler.upload(relativePath, file.getContentType(), file.getInputStream());
        File uploadedApkFile = new File(this.fileProperties.getLocal().getDirectory() + relativePath);
        try (ApkFile apkFile = new ApkFile(uploadedApkFile)) {
            ApkMeta apkMeta = apkFile.getApkMeta();
            log.info("Name: {}", apkMeta.getName());
            log.info("Label: {}", apkMeta.getLabel());
            log.info("PackageName: {}", apkMeta.getPackageName());
            log.info("VersionName: {}", apkMeta.getVersionName());
            log.info("VersionCode: {}", apkMeta.getVersionCode());
            List<Icon> icons = apkFile.getAllIcons().stream()
                    .filter(s -> s instanceof Icon)
                    .map(s -> (Icon) s).filter(s -> 640 == s.getDensity())
                    .collect(Collectors.toList());
            String iconUrl = null;
            if (0 < icons.size()) { // 正确获取到了应用图标
                String fileIdIcon = createFileId();
                String fileNameIcon = fileIdIcon + ".png";
                String relativePathIcon = createRelativeDirectory() + fileNameIcon;
                java.nio.file.Path storePath = Path.of(fileProperties.getLocal().getDirectory(), relativePathIcon);
                FileUtils.writeByteArrayToFile(storePath.toFile(), icons.get(0).getData());
                iconUrl = this.fileHandler.getUrl(relativePathIcon);
            }
            log.info("IconUrl: {}", iconUrl);

            versionDO = VersionDO.builder()
                    .name(apkMeta.getName())
                    .packageName(apkMeta.getPackageName())
                    .versionNum(apkMeta.getVersionCode().intValue())
                    .versionCode(apkMeta.getVersionName())
//                    .mainActivity(apkMeta.get)
                    .url(this.fileHandler.getUrl(relativePath))
                    .iconUrl(iconUrl)
                    .size(uploadedApkFile.length())
                    .checksum(DigestUtils.sha256Hex(new FileInputStream(uploadedApkFile)))
                    .build();
        }
        return success(VersionConvert.INSTANCE.convert(versionDO));
    }

    /**
     * 生成文件id
     *
     * @return
     */
    private String createFileId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成新的文件名
     *
     * @param fileId
     * @param originalFileName
     * @return
     */
    public String rename(String fileId, String originalFileName) {
        return fileId + getFileSuffix(originalFileName);
    }

    /**
     * 获取文件后缀
     * @param fileName 包含后缀
     * @return
     */
    public String getFileSuffix(String fileName) {
        Assert.hasText(fileName, "fileName must not be empty");
        String suffix = "";
        int lastIndex = -1;
        if (-1 != (lastIndex = fileName.lastIndexOf("."))) {
            suffix = fileName.substring(lastIndex);
        }
        return suffix;
    }

    /**
     * 生成相对目录
     * @return
     */
    private String createRelativeDirectory() {
        LocalDate now = LocalDate.now();
        return "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/";
    }
}
