package cn.iocoder.yudao.adminserver.modules.dors.service.easydarwin;

public interface EasyDarwinService {

    /**
     * 启动RTSP拉转推流
     * @param sourceStream RTSP流源地址
     * @param destPath 转推流的路径
     * @return 返回拉流的ID
     */
    String startPushStream(String sourceStream, String destPath);

    /**
     * 停止RTSP拉转推流
     * @param id 拉流的ID
     * @return OK 表示成功，其它表示失败
     */
    String stopPushStream(String id);
}
