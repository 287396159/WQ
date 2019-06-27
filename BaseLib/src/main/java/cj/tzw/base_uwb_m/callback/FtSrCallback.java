package cj.tzw.base_uwb_m.callback;

public interface FtSrCallback {

    void ftSended();

    void ftSendFail(String failMsg);

    void ftRecevied(byte[] recevieMsg);

    void ftRecevieOutTime();
}
