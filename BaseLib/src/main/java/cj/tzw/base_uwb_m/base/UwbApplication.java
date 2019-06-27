package cj.tzw.base_uwb_m.base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public class UwbApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(this);
    }
}
