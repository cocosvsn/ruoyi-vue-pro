package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.operationVideo;

import cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.videoFile.VideoFileDO;
import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

import java.util.List;

/**
 * 手术视频 DO
 *
 * @author Bunco
 */
@TableName("dors_operation_video")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationVideoDO extends BaseDO {

    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 所属手术室
     */
    private Integer room;
    /**
     * 手术名称
     */
    private String title;
    /**
     * 视频截图
     */
    private String poster;
    /**
     * 医生
     */
    private String doctor;
    /**
     * 患者
     */
    private String patient;
    /**
     * 手术简介
     */
    private String operationInfo;
    /**
     * 上线状态
     */
    private Boolean onlineStatus;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 手术视频文件列表
     */
    @TableField(exist = false)
    private List<VideoFileDO> videoFiles;

}
