package cj.tzw.base_uwb_m.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cj.tzw.base_uwb_m.R;


public class DialogUtil {
    public static Dialog adWait = null;
    private static AnimEndThread endThread = null;
    public static int WAIT_DIALOG = 0;
    public static int OK_DIALOG = 1;
    public static int ERROR_DIALOG = 2;
    private static Context context;

    public static void showOrCloseWait(Activity context, int dialogType, String promptMsg){
        if(adWait==null){
            showWait(context,dialogType,promptMsg);
        }else{
            closeWait();
        }
    }

    /**
     * 显示弹框
     * @param context
     * @param dialogType
     * @param promptMsg
     */
    public static void showWait(Context context, int dialogType, String promptMsg){
        endThread = null;
        dialogDismiss();
        final FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.dialog_wait,null);
        ImageView ivWait = view.findViewById(R.id.ivWait);
        TextView tvPromp = view.findViewById(R.id.tvPromp);
        ProgressBar pb = view.findViewById(R.id.pb);
        //ImageView ivClose = view.findViewById(R.id.ivClose);
        adWait = new Dialog(context);
        adWait.setCanceledOnTouchOutside(false);
        Window window = adWait.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        adWait.setContentView(view);
        //Log.e("FFF","宽高："+view.getChildAt(0).getLayoutParams().width+"!"+view.getChildAt(0).getLayoutParams().height);
        window.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,view.getChildAt(0).getLayoutParams().height,context.getResources().getDisplayMetrics());
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,view.getChildAt(0).getLayoutParams().height,context.getResources().getDisplayMetrics());
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        adWait.show();
        pb.setVisibility(View.GONE);
        ivWait.setVisibility(View.VISIBLE);
        AnimatedVectorDrawable drawable = null;
        Drawable imgDrawable = null;
        if(dialogType==OK_DIALOG){
            endThread = new AnimEndThread(1500);
            endThread.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (AnimatedVectorDrawable) context.getResources().getDrawable(R.drawable.vector_anim_ok);
            }else{
                imgDrawable = context.getResources().getDrawable(R.mipmap.ok);
            }
            tvPromp.setTextColor(Color.GREEN);

        }else if(dialogType==ERROR_DIALOG){
            endThread = new AnimEndThread(1500);
            endThread.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (AnimatedVectorDrawable) context.getResources().getDrawable(R.drawable.vector_anim_error);
            }else{
                imgDrawable = context.getResources().getDrawable(R.mipmap.fail);
            }
            tvPromp.setTextColor(Color.RED);
        }else{
            tvPromp.setTextColor(Color.RED);
            pb.setVisibility(View.VISIBLE);
            ivWait.setVisibility(View.GONE);
        }
        tvPromp.setText(promptMsg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && dialogType!=WAIT_DIALOG) {
            ivWait.setImageDrawable(drawable);
            drawable.start();
        }else{
            ivWait.setImageDrawable(imgDrawable);
        }
    }

    public static void dialogDismiss(){
        if(adWait!=null){
            adWait.dismiss();
        }
    }

    /**
     * 关闭弹框
     */
    public static void closeWait(){
        if(adWait!=null){
            if(adWait.isShowing()){
                adWait.dismiss();
            }
        }
    }

    public static void releaseWait(){
        if(adWait!=null){
            adWait = null;
        }
    }

    /**
     * 自动关闭弹框
     */
    static class AnimEndThread extends Thread {
        int sleepTime = 2500;

        public AnimEndThread(int sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            super.run();
            try {
                sleep(sleepTime);
                closeWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
