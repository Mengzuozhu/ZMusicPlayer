package com.mzz.zmusicplayer.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.mzz.zmusicplayer.MyApplication;

import lombok.Data;

/**
 * The type App setting.
 */
@Data
public class AppSetting {

    private static final String APP_SETTING = "AppSetting";
    private static final String PLAY_MODE = "PLAY_MODE";
    private static final String SONG_ORDER_MODE = "SONG_ORDER_MODE";
    private static final String LAST_PLAY_SONG_INDEX = "LAST_PLAY_SONG_INDEX";
    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences =
                    MyApplication.getInstance().getApplicationContext().getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    /**
     * Gets last play mode.
     *
     * @return the last play mode
     */
    public static PlayedMode getPlayMode() {
        int playModeId = getSharedPreferences().getInt(PLAY_MODE, 0);
        return PlayedMode.values()[playModeId];
    }

    /**
     * Sets play mode.
     *
     * @param playMode the play mode
     */
    public static void setPlayMode(PlayedMode playMode) {
        putInt(PLAY_MODE, playMode.getId());
    }

    /**
     * Gets song sort mode.
     *
     * @return the song sort mode
     */
    public static SongOrderMode getSongSortMode() {
        int orderModeId = getSharedPreferences().getInt(SONG_ORDER_MODE, 0);
        return SongOrderMode.values()[orderModeId];
    }

    /**
     * Sets song order mode.
     *
     * @param songOrderMode the song order mode
     */
    public static void setSongOrderMode(SongOrderMode songOrderMode) {
        putInt(SONG_ORDER_MODE, songOrderMode.getId());
    }

    /**
     * Gets last play song index.
     *
     * @return the last play song index
     */
    public static int getLastPlaySongIndex() {
        return getSharedPreferences().getInt(LAST_PLAY_SONG_INDEX, 0);
    }

    /**
     * Sets last play song index.
     *
     * @param playSongIndex the play song index
     */
    public static void setLastPlaySongIndex(int playSongIndex) {
        putInt(LAST_PLAY_SONG_INDEX, playSongIndex);
    }

    private static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

}
