package cn.iocoder.yudao.adminserver.modules.dors.dal.dataobject.live;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 直播 DO
 *
 * @author bunco
 */
@TableName("dors_live")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveDO extends BaseDO {

    /**
     * 主键（自增）
     */
    @TableId
    private Integer id;
    /**
     * 直播流名称
     */
    private String name;
    /**
     * 封面图
     */
    private String icon;
    /**
     * 地址
     */
    private String addr;
    /**
     * 直播流地址
     */
    private String url;
    /**
     * 部门
     */
    private Integer deptId;
    /**
     * 备注
     */
    private String remarks;

}
