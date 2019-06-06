package com.mzz.zmusicplayer.common;

import android.util.Log;

import com.mzz.zandroidcommon.common.StringHelper;

/**
 * author : Mzz
 * date : 2019 2019/6/6 9:48
 * description :
 */
public class TimeRecorder {

    private static long beginTime;

    /**
     * Begin.
     */
    public static void begin() {
        beginTime = System.currentTimeMillis();
    }

    /**
     * End string.
     *
     * @return the string
     */
    public static String end() {
        long endTime = System.currentTimeMillis();
        return StringHelper.getLocalFormat("耗时：%d ms", endTime - beginTime);
    }

    /**
     * Log end.
     */
    public static void logEnd() {
        Log.d("TimeRecorder", end());
    }
}
