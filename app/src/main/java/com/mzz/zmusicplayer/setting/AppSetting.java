package com.mzz.zmusicplayer.setting;

import android.content.Context;
import android.content.SharedPreferences;

import lombok.Data;

/**
 * The type App setting.
 */
@Data
public class AppSetting {

    private static final String APP_SETTING = "AppSetting";
    private static final String PLAY_MODE = "PLAY_MODE";
    private static final String PLAY_SONG_INDEX = "PLAY_SONG_INDEX";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
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

    public static int getLastPlaySongIndex(Context context) {
        return getSharedPreferences(context).getInt(PLAY_SONG_INDEX, 0);
    }

    public static void setLastPlaySongIndex(Context context, int playSongIndex) {
        getSharedPreferences(context).edit().putInt(PLAY_SONG_INDEX, playSongIndex).apply();
    }

}
