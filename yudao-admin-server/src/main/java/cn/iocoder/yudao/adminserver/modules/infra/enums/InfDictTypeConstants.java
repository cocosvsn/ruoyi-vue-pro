package cn.iocoder.yudao.adminserver.modules.infra.enums;

/**
 * Infra 字典类型的枚举类
 *
 * @author 芋道源码
 */
public interface InfDictTypeConstants {

    String REDIS_TIMEOUT_TYPE = "inf_redis_timeout_type"; // Redis 超时类型

    String JOB_STATUS = "inf_job_status"; // 定时任务状态的枚举
    String JOB_LOG_STATUS = "inf_job_log_status"; // 定时任务日志状态的枚举

    String API_ERROR_LOG_PROCESS_STATUS = "inf_api_error_log_process_status"; // API 错误日志的处理状态的枚举

    String ERROR_CODE_TYPE = "inf_error_code_type"; // 错误码的类型枚举

    // 手术视频默认上下线状态
    String DEFAULT_OPERATION_VIDEO_ONLINE_STATUS = "operation.video.online.status";
    // 服务器IP地址，用于配置服务器拉流与录制手术视频的播放地址。
    String SERVER_IP = "aov.server.ip";
}
