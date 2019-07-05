package cj.tzw.base_uwb_m.utils;

import cj.tzw.base_uwb_m.callback.FtReceiveHint;
import cj.tzw.base_uwb_m.model.Device;

public class FtHandleUtil implements FtReceiveHint {
    private final String TAG = "FtHandleUtil";
    private FtBaseUtil ftBaseUtil;
    private FtComUtil ftComUtil;
    private Device device;
    private int deviceType=-1;
    private boolean isReceived = false;

    public FtHandleUtil(FtBaseUtil ftBaseUtil,Device device) {
        this.ftBaseUtil = ftBaseUtil;
        ftComUtil = new FtComUtil(device);
        if(ftBaseUtil!=null){
            ftBaseUtil.setFtHandlerReceviedCallback(this);
        }
        if(device!=null){
            this.device = device;
            deviceType = device.getType();
        }
    }

    public Device getDevice(){
        return this.device;
    }

    public void setDevice(Device device){
        if(device!=null){
            this.device = device;
            deviceType = device.getType();
        }
    }

    /**
     * 搜索设备列表
     */
    public void searchAllDevice(){
        ftBaseUtil.sendFt(ftComUtil.searchUwbDeviceCommand());
    }

    /*查询卡片设备类型*/
    public void searchCardDevice(){
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x00));
    }

    /**
     * 查询卡片软件版本号
     */
    public void readCardId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x03));
    }

    public void readCardEnterLowPowerTime(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x09));
    }

    public void readCardLowFreqDistance(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x0B));
    }
    public void readCardAlarmEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x07));
    }
    public void readCardPowerOffEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x10));
    }
    public void readCardPanId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x0D));
    }

    public void setCardId(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x04,bytes));
    }

    public void setCardEnterLowPowerTime(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x0A,bytes));
    }

    public void setCardLowFreqDistance(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x0C,bytes));
    }
    public void setCardAlarmEn(byte en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x08,new byte[]{en}));
    }
    public void setCardPowerOffEn(byte en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x11,new byte[]{en}));
    }
    public void setCardPanId(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0x0E,bytes));
    }


    public void resetCardPanID(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand((byte)0x0F));
    }

    public void setReponseTagPowerOff(byte en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setCardTypeCommand((byte)0xA1,new byte[]{en}));
    }
    /**
     * 读取卡片所有数据
     */
    public void readCardDeviceAllInfo(){
        final byte[] orderBytes = {0x01,0x02,0x03,0x07,0x09,0x0B,0x0D,0x10};
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(byte order : orderBytes){
                    try {
                        isReceived = false;
                        ftBaseUtil.sendFt(ftComUtil.readCardTypeCommand(order));
                        for(int i=0;i<20;i++){
                            Thread.sleep(10);
                            if(isReceived){
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    /**
     * 读取设备所有数据
     */
    public void readDeviceAllInfo(){
        if(deviceType==-1){
            return;
        }
        byte[] orderBytes = null;
        if(deviceType==1){
            orderBytes = ftComUtil.FORKLIFT_READ_ORDER;
        }else if(deviceType==2){
            orderBytes = ftComUtil.FIX_READ_ORDER;
        }else{
            return;
        }
        final byte[] finalOrderBytes = orderBytes;
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(byte order : finalOrderBytes){
                    try {
                        isReceived = false;
                        ftBaseUtil.sendFt(ftComUtil.readTypeCommand(order));
                        for(int i=0;i<20;i++){
                            Thread.sleep(10);
                            if(isReceived){
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 复位叉车报警器ID
     */
    public void resetForkLiftAlerter(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x41));
    }


    /**
     * 设置叉车报警器ID
     * @param Id
     */
    public void setForkLiftAlerterId(String Id){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x40,ByteUtil.toBytes(Id)));
    }

    /**
     * 设置卡片报警器警报使能位
     * @param en
     */
    public void setTagAlarmEn(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x47,new byte[]{(byte) en}));
    }

    /**
     * 读取卡片报警器报警使能位
     */
    public void readTagAlarmEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x46));
    }

    /**
     * 设置卡片报警器报警距离
     * @param range
     */
    public void setTagAlarmRange(short range){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x49,ByteUtil.getBytes(range,true)));
    }

    /**
     * 读取卡片报警器报警距离
     */
    public void readTagAlarmRange(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x48));
    }

    /**
     * 设置卡片安全距离
     * @param range
     */
    public void setTagSafeRange(short range){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x56,ByteUtil.getBytes(range,true)));
    }

    /**
     * 读取卡片报警器安全距离
     */
    public void readTagSafeRange(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x55));
    }

    /**
     * 设置卡片报警器安全距离延长
     * @param rangeExtend
     */
    public void setTagSafeRangeExtend(short rangeExtend){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x58,ByteUtil.getBytes(rangeExtend,true)));
    }

    /**
     * 读取卡片报警器安全距离延长
     */
    public void readTagSafeRangeExtend(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x57));
    }

    /**
     * 设置叉车报警器报警使能位
     * @param en
     */
    public void setForkLiftAlarmEn_fork(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x4B,new byte[]{(byte) en}));
    }

    /**
     * 读取叉车报警器报警使能位
     */
    public void readForkLiftAlarmEn_fork(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x4A));
    }

    /**
     * 设置叉车报警器报警距离
     * @param range
     */
    public void setForkLiftAlarmRange_fork(short range){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x4D,ByteUtil.getBytes(range,true)));
    }

    /**
     * 读取叉车报警器报警距离
     */
    public void readForkLiftAlarmRange_fork(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x4C));
    }


    /**
     * 设置固定报警器使能位
     * @param en
     */
    public void setFixAlarmEn(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x4F,new byte[]{(byte) en}));
    }

    /**
     * 读取固定报警器使能位
     */
    public void readFixAlarmEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x4E));
    }
    /**
     * 设置降频距离
     * @param distance
     */
    public void setLowFreqDistance(short distance){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x51,ByteUtil.getBytes(distance,true)));
    }

    /**
     * 读取降频距离
     */
    public void readLowFreqDistance(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x50));
    }

    /**
     * 设置叉车报警器PanId
     * @param Id
     */
    public void setForkPanId(String Id){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x53,ByteUtil.toBytes(Id)));
    }

    /**
     * 读取叉车报警器PanId
     */
    public void readForkPanId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x52));
    }

    /**
     * 重置叉车报警器PanId
     */
    public void resetForkPanId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x54));
    }

    /**
     * 复位固定报警器
     */
    public void resetFixAlerter(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x81));
    }

    /**
     * 设置固定报警器ID
     * @param Id
     */
    public void setFixAlerterId(String Id){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x80,ByteUtil.toBytes(Id)));
    }

    /**
     * 设置叉车报警器使能位
     * @param en
     */
    public void setForkLiftAlarmEn_fix(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x85,new byte[]{(byte) en}));
    }

    /**
     * 读取叉车报警器使能位
     */
    public void readForkLiftAlarmEn_fix(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x84));
    }

    /**
     * 设置叉车报警器报警距离
     * @param range
     */
    public void setForkLiftAlarmRange_fix(short range){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x87,ByteUtil.getBytes(range,true)));
    }

    /**
     * 读取叉车报警器报警距离
     */
    public void readForkLiftAlarmRange_fix(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x86));
    }

    /**
     * 设置固定报警器PanId
     * @param Id
     */
    public void setFixPanId(String Id){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0x89,ByteUtil.toBytes(Id)));
    }

    /**
     * 读取固定报警器PanId
     */
    public void readFixPanId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x88));
    }

    /**
     * 重置固定报警器PanId
     */
    public void resetFixPanId(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0x8A));
    }

    /**
     * 设置报警灯使能位
     * @param en
     */
    public void setLedAlarmEn(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xFD,new byte[]{(byte) en}));
    }

    /**
     * 读取报警灯使能位
     */
    public void readLedAlarmEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xFE));
    }

    /**
     * 设置报警声使能位
     * @param en
     */
    public void setSoundAlarmEn(int en){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xFB,new byte[]{(byte) en}));
    }

    /**
     * 读取报警声使能位
     */
    public void readSoundAlarmEn(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xFC));
    }

    /**
     * 设置报警声音大小
     * @param volume
     */
    public void setSoundVolume(int volume){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xF9,new byte[]{(byte) volume}));
    }

    /**
     * 读取报警声音大小
     */
    public void readSoundVolume(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xFA));
    }

    /**
     * 设置报警声模式
     * @param mode
     */
    public void setSoundAlarmMode(int mode){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xF7,new byte[]{(byte) mode}));
    }

    /**
     * 读取报警声模式
     */
    public void readSoundAlarmMode(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xF8));
    }

    /**
     * 设置警报开启时间
     * @param onTime
     */
    public void setFixedModeOnTime(int onTime){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xF5,new byte[]{(byte) onTime}));
    }

    /**
     * 读取警报开启时间
     */
    public void readFixedModeOnTime(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xF6));
    }

    /**
     * 设置警报关闭时间
     * @param offTime
     */
    public void setFixedModeOffTime(int offTime){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setTypeCommand((byte)0xF3,new byte[]{(byte) offTime}));
    }

    /**
     * 读取警报关闭时间
     */
    public void readFixedModeOffTime(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.readTypeCommand((byte)0xF4));
    }


    @Override
    public void ftReceivedHint() {
        isReceived = true;
    }

    /**
     * 上位读取Dongle的状态
     */
    public void readDongleStatus(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setDongleTypeCommand((byte)0x41));
    }

    /**
     * 上位读取Dongle的状态
     */
    public void stopDongleUpdate(){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setDongleTypeCommand((byte)0x42));
    }

    /**
     * 上位请求传输固件到Dongle
     * 发送请求
     */
    public void requestTransFirmWare(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setDongleTypeCommand((byte)0x01,bytes));
    }

    /**
     * 上位开始传输固件
     */
    public void startTransFirmWare(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setDongleTypeCommand((byte)0x02,bytes));
    }


    /**
     * 上位传输固件数据完成
     */
    public void transFirmWareComplete(byte[] bytes){
        isReceived = false;
        ftBaseUtil.sendFt(ftComUtil.setDongleTypeCommand((byte)0x03,bytes));
    }













}
