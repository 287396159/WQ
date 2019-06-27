package cj.tzw.base_uwb_m.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class VerifyUtil {


    public static boolean verifyDeviceId(String idStr){
        if(!TextUtils.isEmpty(idStr)){
            return idStr.matches("[1-9A-Fa-f]{4}");
        }
        return false;
    }
    public static boolean verifyExcludeId(String idStr){
        if(!TextUtils.isEmpty(idStr)){
            if("0000".equals(idStr)||"ffff".equalsIgnoreCase(idStr)){
                return false;
            }
            return idStr.matches("[0-9A-Fa-f]{4}");
        }
        return false;
    }

    public static boolean verifyRangeExcludeZero(String rangeStr){
        if(!TextUtils.isEmpty(rangeStr)){
            int range = Integer.valueOf(rangeStr);
            if(range>0 && range<=65535){
                return true;
            }
        }
        return false;
    }

    public static boolean verifyRangeIncludeZero(String rangeStr){
        if(!TextUtils.isEmpty(rangeStr)){
            int range = Integer.valueOf(rangeStr);
            if(range>=0 && range<=65535){
                return true;
            }
        }
        return false;
    }

    public static boolean verifyOnOrOffTime(String timeStr){
        if(!TextUtils.isEmpty(timeStr)){
            int time = Integer.valueOf(timeStr);
            if(time>=0 && time<=255){
                return true;
            }
        }
        return false;
    }


}
