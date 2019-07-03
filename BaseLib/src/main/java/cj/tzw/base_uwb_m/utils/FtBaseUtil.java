package cj.tzw.base_uwb_m.utils;

import android.content.Context;
import android.util.Log;

import cj.tzw.base_uwb_m.callback.FtOcCallback;
import cj.tzw.base_uwb_m.callback.FtReceiveHint;
import cj.tzw.base_uwb_m.callback.FtSrCallback;
import ftapi.DataCallBack;
import ftapi.FtLib;

public class FtBaseUtil {
    private final String TAG = "FtBaseUtil";
    private static FtBaseUtil ftBaseUtil;
    private static FtLib ftLib;
    private static FtOcCallback ftOcCallback;
    private static FtSrCallback ftSrCallback;
    private static FtReceiveHint handlerReceiveHint;
    private static FtReceiveHint monitorReceiveHint;
    private FtOutTimeMonitor ftOutTimeMonitor;
    private StringBuffer sb;

    private DataCallBack callBack = new DataCallBack() {
        @Override
        public void method(char[] chars) {
            String strChar="";
            for(int j=0;j<chars.length;j++){
                if((byte)chars[j]<0){
                    strChar+="|"+(256+(byte)chars[j]);
                }else{
                    strChar+="|"+(byte)chars[j];
                }

            }
            Log.i(TAG,"接受数据！"+strChar);
            String charStr = ByteUtil.CharToString(chars,chars.length).replaceAll(" ","").toUpperCase();
            Log.i(TAG,"接受数据："+charStr.toLowerCase()+"-"+ftSrCallback+"-"+ftOcCallback+"-"+monitorReceiveHint);
            if(charStr.startsWith("F1")){
                if(charStr.endsWith("1F")){
                    if(ftSrCallback!=null){
                        ftSrCallback.ftRecevied(ByteUtil.toBytes(charStr));
                        if(handlerReceiveHint !=null){
                            handlerReceiveHint.ftReceivedHint();
                        }
                    }
                    if(ftOcCallback!=null){
                        ftOcCallback.ftRecevied(ByteUtil.toBytes(charStr));
                    }
                    if(monitorReceiveHint !=null){
                        monitorReceiveHint.ftReceivedHint();
                    }
                }else{
                    sb = new StringBuffer();
                    sb.append(charStr);
                }
            }else{
                if(charStr.startsWith("D1")){
                    if(charStr.endsWith("1D")){
                        if(ftSrCallback!=null){
                            ftSrCallback.ftRecevied(ByteUtil.toBytes(charStr));
                            if(handlerReceiveHint !=null){
                                handlerReceiveHint.ftReceivedHint();
                            }

                        }
                        if(ftOcCallback!=null){
                            ftOcCallback.ftRecevied(ByteUtil.toBytes(charStr));
                        }
                        if(monitorReceiveHint !=null){
                            monitorReceiveHint.ftReceivedHint();
                        }
                    }else{
                        sb = new StringBuffer();
                        sb.append(charStr);
                    }
                }else{
                    if(charStr.startsWith("E9")){
                        if(charStr.endsWith("9E")){
                            if(ftSrCallback!=null){
                                ftSrCallback.ftRecevied(ByteUtil.toBytes(charStr));
                                if(handlerReceiveHint !=null){
                                    handlerReceiveHint.ftReceivedHint();
                                }

                            }
                            if(ftOcCallback!=null){
                                ftOcCallback.ftRecevied(ByteUtil.toBytes(charStr));
                            }
                            if(monitorReceiveHint !=null){
                                monitorReceiveHint.ftReceivedHint();
                            }
                        }else{
                            Log.i(TAG,"Null数据："+sb+"-"+charStr);
                            sb = new StringBuffer();
                            sb.append(charStr);
                            if(ftSrCallback!=null){
                                ftSrCallback.ftRecevied(ByteUtil.toBytes(charStr));
                            }
                            if(ftOcCallback!=null){
                                ftOcCallback.ftRecevied(ByteUtil.toBytes(charStr));
                            }

                        }
                    }else{
                        Log.i(TAG,"Null数据："+sb+"-"+charStr);
                        sb = new StringBuffer();
                        sb.append(charStr);
                        if(ftSrCallback!=null){
                            ftSrCallback.ftRecevied(ByteUtil.toBytes(charStr));
                        }
                        if(ftOcCallback!=null){
                            ftOcCallback.ftRecevied(ByteUtil.toBytes(charStr));
                        }
                    }
                }
            }
        }
    };

    public FtBaseUtil(Context context) {
        ftLib = FtLib.getInstance(context,callBack);
    }

    public static FtBaseUtil getInstance(Context context){
        if(ftBaseUtil==null){
            ftBaseUtil = new FtBaseUtil(context);
        }
        return ftBaseUtil;
    }

    public FtBaseUtil setFtOcCallback(FtOcCallback ftOcCallback){
        this.ftOcCallback = ftOcCallback;
        ftSrCallback = null;
        return ftBaseUtil;
    }

    public FtBaseUtil setFtSrCallback(FtSrCallback ftSrCallback){
        this.ftSrCallback = ftSrCallback;
        ftOcCallback = null;
        return ftBaseUtil;
    }

    public FtBaseUtil setFtHandlerReceviedCallback(FtReceiveHint handlerReceiveHint) {
        this.handlerReceiveHint = handlerReceiveHint;
        return ftBaseUtil;
    }


    public FtBaseUtil setFtMonitorReceviedCallback(FtReceiveHint monitorReceiveHint) {
        this.monitorReceiveHint = monitorReceiveHint;
        return ftBaseUtil;
    }

    /**
     * 打开Ft
     */
    public void openFt(){
        if(ftOcCallback==null){
            return;
        }
        int ret = ftLib.createDeviceList();
        if(ret>0){
            ret = ftLib.connectFt();
            Log.i(TAG,"连接的ret："+ret);
            if(ret==0||ret==-3){
                ret = ftLib.setConfig(115200, (byte)8,(byte) 0, (byte)0, (short) 0);
                if(ret==0){
                    ftOcCallback.ftOpened();
                }else
                    ftOcCallback.ftOpenFail("");
            }else
                ftOcCallback.ftOpenFail("");
        }else
            ftOcCallback.ftOpenFail("");

    }

    /**
     * 关闭Ft
     */
    public void closeFt(){
        if(ftLib!=null){
            Log.i(TAG," 关闭Ft！");
            ftLib.disConnectFt();
            if(ftOcCallback!=null){
                ftOcCallback.ftClosed();
            }
        }
//        ftLib = null;
//        ftBaseUtil = null;
        ftOcCallback = null;
        ftSrCallback = null;
        handlerReceiveHint = null;
        monitorReceiveHint = null;
    }

    /**
     * 发送Ft
     * @param sendBytes
     */
    public void sendFt(byte[] sendBytes){
        if(ftSrCallback==null && ftOcCallback==null){
            return;
        }
        int ret = ftLib.SendMessage(sendBytes,sendBytes.length);
        if(ret>0){
            if(ftSrCallback!=null){
                ftSrCallback.ftSended();
            }
            if(ftOcCallback!=null){
                ftOcCallback.ftSended();
            }
            ftOutTimeMonitor = new FtOutTimeMonitor();
            ftOutTimeMonitor.setFtOcCallback(ftOcCallback);
            ftOutTimeMonitor.setFtSrCallback(ftSrCallback);
            monitorReceiveHint = ftOutTimeMonitor;
            new Thread(ftOutTimeMonitor).start();
        }else{
            if(ftSrCallback!=null){
                ftSrCallback.ftSendFail("");
            }
            if(ftOcCallback!=null){
                ftOcCallback.ftSendFail("");
            }
        }
    }




}
