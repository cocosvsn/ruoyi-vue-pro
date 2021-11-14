package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 手术视频文件 DO
 *
 * @author Bunco
 */
@TableName("dors_video_file")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoFileDO extends BaseDO {

    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 关联手术
     */
    private Integer operationVideo;
    /**
     * 标题
     */
    private String title;
    /**
     * 文件类型
     */
    private String contentType;
    /**
     * 文件大小
     */
    private Integer fileSize;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 备注
     */
    private String remarks;

}
