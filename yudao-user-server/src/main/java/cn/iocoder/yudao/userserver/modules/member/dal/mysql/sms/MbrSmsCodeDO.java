package cn.iocoder.yudao.userserver.modules.member.dal.mysql.sms;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 手机验证码 DO
 *
 * idx_mobile 索引：基于 {@link #mobile} 字段
 *
 * @author 芋道源码
 */
@TableName("mbr_sms_code")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MbrSmsCodeDO extends BaseDO {

    /**
     * 编号
     */
    private Integer id;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String code;
    /**
     * 发送场景
     *
     * 枚举 {@link MbrSmsCodeDO}
     */
    private Integer scene;
    /**
     * 创建 IP
     */
    private String createIp;
    /**
     * 今日发送的第几条
     */
    private Integer todayIndex;
    /**
     * 是否使用
     */
    private Boolean used;
    /**
     * 使用时间
     */
    private Date usedTime;
    /**
     * 使用 IP
     */
    private String usedIp;

}
