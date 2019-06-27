package cj.tzw.base_uwb_m.callback;

public interface FtOcCallback {

//    串口已打开
    void ftOpened();

//    串口打卡失败
    void ftOpenFail(String failMsg);

//    串口关闭
    void ftClosed();

//    串口已发送
    void ftSended();

//    串口发送失败
    void ftSendFail(String failMsg);

//    串口已接收
    void ftRecevied(byte[] receviedBytes);

//    串口超时
    void ftRecevieOutTime();

}
