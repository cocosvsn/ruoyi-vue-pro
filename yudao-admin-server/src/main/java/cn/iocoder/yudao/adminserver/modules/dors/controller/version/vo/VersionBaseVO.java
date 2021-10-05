package cn.iocoder.yudao.adminserver.modules.dors.controller.version.vo;

import lombok.*;
import io.swagger.annotations.*;
import javax.validation.constraints.*;

/**
* 应用版本 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class VersionBaseVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "包名")
    private String packageName;

    @ApiModelProperty(value = "版本号", required = true)
    @NotNull(message = "版本号不能为空")
    private Integer versionNum;

    @ApiModelProperty(value = "版本代码")
    private String versionCode;

    @ApiModelProperty(value = "是否强制升级", required = true)
    @NotNull(message = "是否强制升级不能为空")
    private Boolean forceUpdate;

    @ApiModelProperty(value = "主入口Activity")
    private String mainActivity;

    @ApiModelProperty(value = "开发者")
    private String author;

    @ApiModelProperty(value = "开发者联系方式")
    private String authorContact;

    @ApiModelProperty(value = "描述")
    private String description;

}
