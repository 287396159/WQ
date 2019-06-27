package cj.tzw.base_uwb_m.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void toastShortMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }



    public static void toastLongMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }



}
