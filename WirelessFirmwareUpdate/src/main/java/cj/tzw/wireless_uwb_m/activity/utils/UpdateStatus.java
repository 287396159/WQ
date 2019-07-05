package cj.tzw.wireless_uwb_m.activity.utils;

public class UpdateStatus {

    private byte updateStatus;

    public static byte canBeUpdated=0;                       //可以更新
    public static byte updateSuccessful=1;                  //更新成功
    public static byte updateFailed=2;                      //更新失败
    public static byte updateDisableOrNoBin=3;              //dongle没使能更新或不存在固件
    public static byte versionSame=4;                       //版本号相同
    public static byte illegallyFirmware=5;                 //非法固件
    public static byte notSameWithNeedUpdateVersion=60;     //设备版本和指定的需要更新的版本不一样
    public static byte firmwareTooLarge=7;                  //固件太大

    public UpdateStatus(){

    }
    public UpdateStatus(byte updateStatus){
        this.updateStatus=updateStatus;
    }

    public byte getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(byte updateStatus) {
        this.updateStatus = updateStatus;
    }
}
