package com.mzz.zmusicplayer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * @author : Mzz
 * date : 2019 2019/4/26 21:22
 * description :
 */
public class MusicApplication extends Application {
    private static MusicApplication sInstance;

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }

    /**
     * Exit app.
     */
    public static void exitApp() {
        ActivityManager activityManager =
                (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        System.exit(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        CrashReport.initCrashReport(getApplicationContext());
    }

}
