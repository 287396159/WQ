package cj.tzw.base_uwb_m.utils;

import android.util.Log;

import cj.tzw.base_uwb_m.callback.FtOcCallback;
import cj.tzw.base_uwb_m.callback.FtReceiveHint;
import cj.tzw.base_uwb_m.callback.FtSrCallback;

public class FtOutTimeMonitor implements Runnable,FtReceiveHint{
    private final String TAG = "FtOutTimeMonitor";
    private final int OUT_TIME_SECOND = 5000;//5s超时
    private final int PER_TIME = 20;
    private boolean isRecevied = false;
    private FtOcCallback ftOcCallback;
    private FtSrCallback ftSrCallback;

    public FtOutTimeMonitor() {
        Log.i(TAG,"FtOutTimeMonitor");
        isRecevied = false;
    }

    public void setFtOcCallback(FtOcCallback ftOcCallback){
        this.ftOcCallback = ftOcCallback;
    }

    public void setFtSrCallback(FtSrCallback ftSrCallback){
        this.ftSrCallback =ftSrCallback;
    }

    @Override
    public void run() {
        try {
            for(int i=0;i<OUT_TIME_SECOND/PER_TIME;i++){
                Thread.sleep(PER_TIME);
                if(isRecevied){
                    return;
                }
            }
            if(ftOcCallback!=null){
                ftOcCallback.ftRecevieOutTime();
            }
            if(ftSrCallback!=null){
                ftSrCallback.ftRecevieOutTime();
            }
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ftReceivedHint() {
        isRecevied = true;
    }



}
