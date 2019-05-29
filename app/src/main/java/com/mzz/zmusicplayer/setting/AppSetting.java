package com.mzz.zmusicplayer.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.mzz.zandroidcommon.common.JsonConverter;

import lombok.Data;

/**
 * The type App setting.
 */
@Data
public class AppSetting {

    private static final String APP_SETTING = "AppSetting";
    private static final String PLAY_MODE = "PLAY_MODE";
    private int lastPlaySongIndex = 0;
    private PlayedMode playedMode = PlayedMode.ORDER;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
    }

    /**
     * Read setting app setting.
     *
     * @param context the context
     * @return the app setting
     */
    public static AppSetting readSetting(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String jsonSetting = sharedPreferences.getString(APP_SETTING, "");
        AppSetting appSetting = JsonConverter.jsonToClass(jsonSetting, AppSetting.class);
        if (appSetting == null) {
            appSetting = new AppSetting();
        }
        return appSetting;
    }

    public static PlayedMode getLastPlayMode(Context context) {
        String playModeName = getSharedPreferences(context).getString(PLAY_MODE, null);
        if (playModeName != null) {
            return PlayedMode.valueOf(playModeName);
        }
        return PlayedMode.ORDER;
    }

    public static void setPlayMode(Context context, PlayedMode playMode) {
        getSharedPreferences(context).edit().putString(PLAY_MODE, playMode.name()).apply();
    }

    /**
     * Apply setting.
     *
     * @param context the context
     */
    public void applySetting(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String stringValue = JsonConverter.convertToStringValue(this);
        editor.putString(APP_SETTING, stringValue);
        editor.apply();
    }

}
