package cn.iocoder.yudao.adminserver.modules.dors.enums;// ========== 设备 TODO 补充编号 ==========


import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Dors 错误码枚举类
 *
 * Dors 系统，使用 2-001-000-000 段
 */
public interface DorsErrorCodeConstants {

    // ========== ROOM 房间 模块 2001000000 ==========
    ErrorCode ROOM_NOT_EXISTS = new ErrorCode(2001000000, "房间不存在");

    // ========== DEVICE 设备 模块 2001001000 ==========
    ErrorCode DEVICE_NOT_EXISTS = new ErrorCode(2001001000, "设备不存在");

    // ========== CHANNEL 频道 模块 2001002000 ==========
    ErrorCode CHANNEL_NOT_EXISTS = new ErrorCode(2001002000, "频道不存在");

    // ========== VERSION 应用版本 模块 2001003000 ==========
    ErrorCode VERSION_NOT_EXISTS = new ErrorCode(2001003000, "应用版本不存在");

    // ========== VERSION 手术视频 模块 2001004000 ==========
    ErrorCode OPERATION_VIDEO_NOT_EXISTS = new ErrorCode(2001004000, "手术视频不存在");

    // ========== VERSION 手术视频 模块 2001005000 ==========
    ErrorCode VIDEO_FILE_NOT_EXISTS = new ErrorCode(2001005000, "手术视频文件不存在");

}

