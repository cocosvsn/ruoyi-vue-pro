package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.version;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 应用版本 DO
 *
 * @author Bunco
 */
@TableName("dors_version")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDO extends BaseDO {

    /**
     * 编号（自增）
     */
    @TableId
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 版本号
     */
    private Integer versionNum;
    /**
     * 版本代码
     */
    private String versionCode;
    /**
     * 是否强制升级
     */
    private Boolean forceUpdate;
    /**
     * 主入口Activity
     */
    private String mainActivity;
    /**
     * 下载地址
     */
    private String url;
    /**
     * 图标地址
     */
    private String iconUrl;
    /**
     * 校验码
     */
    private String checksum;
    /**
     * 大小
     */
    private Long size;
    /**
     * 开发者
     */
    private String author;
    /**
     * 开发者联系方式
     */
    private String authorContact;
    /**
     * 更新日志
     */
    private String changeLog;
    /**
     * 描述
     */
    private String description;
    /**
     * 浏览次数
     */
    private Integer hits;
    /**
     * 下载次数
     */
    private Integer dlCount;
    /**
     * 备注
     */
    private String remarks;

}
