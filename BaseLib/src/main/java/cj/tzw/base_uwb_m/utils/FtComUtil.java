package cj.tzw.base_uwb_m.utils;

import android.util.Log;

import cj.tzw.base_uwb_m.model.Device;

public class FtComUtil {
    /*SET:
    ForkLift - set :40 47 49 56 58 4B 4D 4F 51 53
    Alarm - set :FD FB F9 F7 F5 F3
    Fix - set :80 85 87 89

    READ:
    ForkLift - set :46 48 55 57 4A 4C 4E 50 52 54
    Alarm - set :FE FC FA F8 F6 F4
    Fix - set :84 86 88 8A*/
    private final String TAG = "FtComUtil";
    public final byte[] FORKLIFT_READ_ORDER = {(byte)0x46,(byte)0x48,(byte)0x55,(byte)0x57,(byte)0x4A,(byte)0x4C,(byte)0x4E,(byte)0x50,(byte)0x52
            ,(byte)0xFE,(byte)0xFC,(byte)0xFA,(byte)0xF8,(byte)0xF6,(byte)0xF4};
    public final byte[] FIX_READ_ORDER = {(byte)0x84,(byte)0x86,(byte)0x88,(byte)0xFE,(byte)0xFC,(byte)0xFA,(byte)0xF8,(byte)0xF6,(byte)0xF4};

    private Device device;

    public FtComUtil(Device device) {
        this.device = device;
    }

    /**
     * 搜索列表设备命令
     * @return
     */
    public byte[] searchUwbDeviceCommand(){
        byte[] comBytes = new byte[4];
        comBytes[0] = (byte)0xE1;
        comBytes[1] = (byte)0x00;
        comBytes[2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[3] = (byte)0x1E;
        return comBytes;
    }

    /**
     * 读取卡片参数命令
     * @param order
     * @return
     */
    public byte[] readCardTypeCommand(byte order){
        byte[] comBytes = new byte[4];
        comBytes[0] = (byte)0xC1;
        comBytes[1] = order;
        comBytes[2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[3] = (byte)0x1C;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"readTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }


    /**
     * dongle参数命令
     * @param order
     * @return
     */
    public byte[] setDongleTypeCommand(byte order){
        byte[] comBytes = new byte[4];
        comBytes[0] = (byte)0xF9;
        comBytes[1] = order;
        comBytes[2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[3] = (byte)0x9F;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"setDongleTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }

    /**
     * dongle参数命令
     * @param order
     * @return
     */
    public byte[] setDongleTypeCommand(byte order,byte[] setBytes){
        byte[] comBytes = new byte[4+setBytes.length];
        comBytes[0] = (byte)0xF9;
        comBytes[1] = order;
        int index = 2;
        for(int i=0;i<setBytes.length;i++){
            comBytes[index] = setBytes[i];
            index++;
        }
        comBytes[comBytes.length-2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[comBytes.length-1] = (byte)0x9F;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"setDongleTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }


    /**
     * 读取卡片参数命令
     * @param order
     * @return
     */
    public byte[] setCardTypeCommand(byte order,byte[] setBytes){
        byte[] comBytes = new byte[4+setBytes.length];
        comBytes[0] = (byte)0xC1;
        comBytes[1] = order;
        int index = 2;
        for(int i=0;i<setBytes.length;i++){
            comBytes[index] = setBytes[i];
            index++;
        }
        comBytes[comBytes.length-2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[comBytes.length-1] = (byte)0x1C;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"readTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }


    /**
     * 设置参数命令
     * @param order
     * @param setBytes
     * @return
     */
    public byte[] setTypeCommand(byte order,byte[] setBytes){
        if(device==null){
            return null;
        }
        byte[] deviceIdBytes = device.getAlerterID();
        byte deviceType = (byte) device.getType();
        byte[] comBytes = new byte[7+setBytes.length];
        comBytes[0] = (byte)0xE1;
        comBytes[1] = order;
        comBytes[2] = deviceIdBytes[0];
        comBytes[3] = deviceIdBytes[1];
        comBytes[4] = deviceType;
        int index = 5;
        for(int i=0;i<setBytes.length;i++){
            comBytes[index] = setBytes[i];
            index++;
        }
        comBytes[comBytes.length-2] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[comBytes.length-1] = (byte)0x1E;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"setTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }

    /**
     * 读取参数命令
     * @param order
     * @return
     */
    public byte[] readTypeCommand(byte order){
        if(device==null){
            return null;
        }
        byte[] deviceIdBytes = device.getAlerterID();
        byte deviceType = (byte) device.getType();
        byte[] comBytes = new byte[7];
        comBytes[0] = (byte)0xE1;
        comBytes[1] = order;
        comBytes[2] = deviceIdBytes[0];
        comBytes[3] = deviceIdBytes[1];
        comBytes[4] = deviceType;
        comBytes[5] = ByteUtil.getCheckBit(comBytes,comBytes.length-2);
        comBytes[6] = (byte)0x1E;
        String comStr = ByteUtil.bytesToHexFun3(comBytes);
        Log.i(TAG,"readTypeCommand：：十六进制命令："+comStr);
        return comBytes;
    }





}
