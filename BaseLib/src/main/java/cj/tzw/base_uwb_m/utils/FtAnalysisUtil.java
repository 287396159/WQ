package cj.tzw.base_uwb_m.utils;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import cj.tzw.base_uwb_m.model.Device;

public class FtAnalysisUtil {

    public static boolean isCorrectData(byte[] dataBytes){
        if(dataBytes!=null && dataBytes.length>2 && dataBytes[0]==(byte)0xF1 && dataBytes[dataBytes.length-1]==(byte)0x1F ){
            return true;
        }
        return false;
    }
    public static boolean isCardCorrectData(byte[] dataBytes){
        if(dataBytes!=null && dataBytes.length>2 && dataBytes[0]==(byte)0xD1 && dataBytes[dataBytes.length-1]==(byte)0x1D ){
            return true;
        }
        return false;
    }

    public static boolean isDongleCorrectData(byte[] dataBytes){
        if(dataBytes!=null && dataBytes.length>2 && dataBytes[0]==(byte)0xE9 && dataBytes[dataBytes.length-1]==(byte)0x9E ){
            return true;
        }
        return false;
    }

    public static HashMap<String,byte[]> analysisDongleData(byte[] dataBytes){
        HashMap<String,byte[]> retMap = null;
        Log.i("FtAnalysisUtil", "analysisData: "+ByteUtil.bytesToHexFun3(dataBytes));
        if(isDongleCorrectData(dataBytes)){
            retMap=new HashMap<>();
            byte order=dataBytes[1];
            int startIndex = 2;
            int endIndex = dataBytes.length-2;
            int dataLen = endIndex-startIndex;
            byte[] retBytes = new byte[dataLen];
            System.arraycopy(dataBytes,startIndex,retBytes,0,dataLen);
            switch (order){
                case (byte)0x41:
                    byte[] dongleFirmwareVersion=new byte[4];
                    System.arraycopy(retBytes,0,dongleFirmwareVersion,0,dongleFirmwareVersion.length);
                    retMap.put("dongle_firmware_version",dongleFirmwareVersion);
                    byte[] askToUpdateEn=new byte[1];
                    System.arraycopy(retBytes,4,askToUpdateEn,0,askToUpdateEn.length);
                    retMap.put("askToUpdate_en",askToUpdateEn);
                    byte[] waitForUpdateEn=new byte[1];
                    System.arraycopy(retBytes,5,waitForUpdateEn,0,waitForUpdateEn.length);
                    retMap.put("waitForUpdate_en",waitForUpdateEn);


                    byte[] firmwareType=new byte[4];
                    System.arraycopy(retBytes,7,firmwareType,0,firmwareType.length);
                    retMap.put("firmware_type",firmwareType);
                    byte[] firmwareVersion=new byte[4];
                    System.arraycopy(retBytes,11,firmwareVersion,0,firmwareVersion.length);
                    retMap.put("firmware_version",firmwareVersion);
                    byte[] firmwareSize=new byte[3];
                    System.arraycopy(retBytes,15,firmwareSize,0,firmwareSize.length);
                    retMap.put("firmware_size",firmwareSize);
                    byte[] needUpdateVersion=new byte[4];
                    System.arraycopy(retBytes,18,needUpdateVersion,0,needUpdateVersion.length);
                    retMap.put("need_update_version",needUpdateVersion);
                    break;
            }
        }
        return retMap;
    }


    public static HashMap<String,byte[]> analysisCardData(byte[] dataBytes){
        HashMap<String,byte[]> retMap = null;
        Log.i("FtAnalysisUtil", "analysisData: "+ByteUtil.bytesToHexFun3(dataBytes));
        if(isCardCorrectData(dataBytes)){
            retMap = new HashMap<>();
            byte order = dataBytes[1];
            int startIndex = 2;
            int endIndex = dataBytes.length-2;
            int dataLen = endIndex-startIndex;
            byte[] retBytes = new byte[dataLen];
            System.arraycopy(dataBytes,startIndex,retBytes,0,dataLen);
            Log.i("FtAnalysisUtil", "analysisData1: "+order);
            switch (order){
                //********************SET********************
                case (byte)0x40:
                    retMap.put("fork_id_set",retBytes);
                    break;
                //********************READ********************
                case (byte)0x01:
                    retMap.put("firmware_version",retBytes);
                    break;
                case (byte)0x02:
                    retMap.put("hardware_version",retBytes);
                    break;
                case (byte)0x03:
                    retMap.put("card_id",retBytes);
                    break;
                case (byte)0x07:
                    retMap.put("alarm_en",retBytes);
                    break;
                case (byte)0x09:
                    retMap.put("enter_low_power_time",retBytes);
                    break;
                case (byte)0x0B:
                    retMap.put("low_power_distance",retBytes);
                    break;
                case (byte)0x0D:
                    retMap.put("pan_id",retBytes);
                    break;
                case (byte)0x10:
                    retMap.put("power_off_en",retBytes);
                    break;
                case (byte)0x0F:
                    retMap.put("pan_id",retBytes);
                    break;
                case (byte)0xA1:
                    retMap.put("reponse_tag_power_off",retBytes);
                    break;
            }
        }
        return retMap;
    }

    /**
     * 解析搜索到的设备数据
     * @param dataBytes
     * @return
     */
    public static ArrayList<Device> analysisDeviceListData(byte[] dataBytes){
        ArrayList<Device> deviceList = null;
        if(isCorrectData(dataBytes)){
            deviceList = new ArrayList<>();
            int deviceCount = dataBytes[2];
            int index = 3;
            for(int i=0;i<deviceCount;i++){
                byte[] idBytes = new byte[2];
                idBytes[0] = dataBytes[index];
                idBytes[1] = dataBytes[index+1];
                byte type = dataBytes[index+2];
                byte[] fVertion = new byte[4];
                fVertion[0] = dataBytes[index+3];
                fVertion[1] = dataBytes[index+4];
                fVertion[2] = dataBytes[index+5];
                fVertion[3] = dataBytes[index+6];
                byte hVertion = dataBytes[index+7];
                Device device = new Device();
                device.setAlerterID(idBytes);
                device.setType(type);
                device.setFirmware_version(ByteUtil.bytesToHexFun3(fVertion));
                device.sethVersion(hVertion+"");
                deviceList.add(device);
                index = index + 8;
            }
        }
        return deviceList;

    }


    public static HashMap<String,byte[]> analysisData(byte[] dataBytes){
        HashMap<String,byte[]> retMap = null;
        Log.i("FtAnalysisUtil", "analysisData: "+ByteUtil.bytesToHexFun3(dataBytes));
        if(isCorrectData(dataBytes)){
            retMap = new HashMap<>();
            byte order = dataBytes[1];
            int startIndex = 5;
            int endIndex = dataBytes.length-2;
            int dataLen = endIndex-startIndex;
            byte[] retBytes = new byte[dataLen];
            System.arraycopy(dataBytes,startIndex,retBytes,0,dataLen);
            Log.i("FtAnalysisUtil", "analysisData: "+order);
            switch (order){
            //********************SET********************
                case (byte)0x40:
                    retMap.put("fork_id_set",retBytes);
                    break;
                case (byte)0x47:
                    retMap.put("tag_alarm_en_set",retBytes);
                    break;
                case (byte)0x49:
                    retMap.put("tag_alarm_range_set",retBytes);
                    break;
                case (byte)0x56:
                    retMap.put("tag_safe_range_set",retBytes);
                    break;
                case (byte)0x58:
                    retMap.put("tag_safe_range_extend_set",retBytes);
                    break;
                case (byte)0x4B:
                    retMap.put("forklift_alarm_en_fork_set",retBytes);
                    break;
                case (byte)0x4D:
                    retMap.put("forklift_alarm_range_fork_set",retBytes);
                    break;
                case (byte)0x4F:
                    retMap.put("fix_alarm_en_set",retBytes);
                    break;
                case (byte)0x51:
                    retMap.put("low_freq_distance_set",retBytes);
                    break;
                case (byte)0x53:
                    retMap.put("pan_id_fork_set",retBytes);
                    break;
                case (byte)0xFD:
                    retMap.put("led_alarm_en_set",retBytes);
                    break;
                case (byte)0xFB:
                    retMap.put("sound_alarm_en_set",retBytes);
                    break;
                case (byte)0xF9:
                    retMap.put("sound_volume_set",retBytes);
                    break;
                case (byte)0xF7:
                    retMap.put("sound_alarm_mode_set",retBytes);
                    break;
                case (byte)0xF5:
                    retMap.put("fixed_mode_ontime_set",retBytes);
                    break;
                case (byte)0xF3:
                    retMap.put("fixed_mode_offtime_set",retBytes);
                    break;
                case (byte)0x80:
                    retMap.put("fix_id_set",retBytes);
                    break;
                case (byte)0x85:
                    retMap.put("forklift_alarm_en_fix_set",retBytes);
                    break;
                case (byte)0x87:
                    retMap.put("forklift_alarm_range_fix_set",retBytes);
                    break;
                case (byte)0x89:
                    retMap.put("pan_id_fix_set",retBytes);
                    break;
            //********************READ********************
                case (byte)0x41:
                    retMap.put("reset_forklift_alerter",retBytes);
                    break;
                case (byte)0x81:
                    retMap.put("reset_fix_alerter",retBytes);
                    break;
                case (byte)0x46:
                    retMap.put("tag_alarm_en_read",retBytes);
                    break;
                case (byte)0x48:
                    retMap.put("tag_alarm_range_read",retBytes);
                    break;
                case (byte)0x55:
                    retMap.put("tag_safe_range_read",retBytes);
                    break;
                case (byte)0x57:
                    retMap.put("tag_safe_range_extend_read",retBytes);
                    break;
                case (byte)0x4A:
                    retMap.put("forklift_alarm_en_fork_read",retBytes);
                    break;
                case (byte)0x4C:
                    retMap.put("forklift_alarm_range_fork_read",retBytes);
                    break;
                case (byte)0x4E:
                    retMap.put("fix_alarm_en_read",retBytes);
                    break;
                case (byte)0x50:
                    retMap.put("low_freq_distance_read",retBytes);
                    break;
                case (byte)0x52:
                    retMap.put("pan_id_fork_read",retBytes);
                    break;
                case (byte)0x54:
                    retMap.put("pan_id_fork_reset",retBytes);
                    break;
                case (byte)0xFE:
                    retMap.put("led_alarm_en_read",retBytes);
                    break;
                case (byte)0xFC:
                    retMap.put("sound_alarm_en_read",retBytes);
                    break;
                case (byte)0xFA:
                    retMap.put("sound_volume_read",retBytes);
                    break;
                case (byte)0xF8:
                    retMap.put("sound_alarm_mode_read",retBytes);
                    break;
                case (byte)0xF6:
                    retMap.put("fixed_mode_ontime_read",retBytes);
                    break;
                case (byte)0xF4:
                    retMap.put("fixed_mode_offtime_read",retBytes);
                    break;
                case (byte)0x84:
                    retMap.put("forklift_alarm_en_fix_read",retBytes);
                    break;
                case (byte)0x86:
                    retMap.put("forklift_alarm_range_fix_read",retBytes);
                    break;
                case (byte)0x88:
                    retMap.put("pan_id_fix_read",retBytes);
                    break;
                case (byte)0x8A:
                    retMap.put("pan_id_fix_reset",retBytes);
                    break;
                case (byte)0x91://set card id
                    retMap.put("set_card_id",retBytes);
                    break;
            }
        }
        return retMap;
    }


}
