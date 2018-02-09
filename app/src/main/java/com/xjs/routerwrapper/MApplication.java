package com.xjs.routerwrapper;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author xjs
 *         on  2018/2/4
 *         desc:
 */

public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       // ARouter.openDebug();
        //ARouter.openLog();
        ARouter.init(this);
    }
}
