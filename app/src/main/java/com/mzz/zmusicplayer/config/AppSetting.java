package com.mzz.zmusicplayer.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.mzz.zmusicplayer.MusicApplication;

/**
 * The type App setting.
 *
 * @author Mengzz
 */
public class AppSetting {

    private static final String APP_SETTING = "AppSetting";
    private static final String PLAY_MODE = "PLAY_MODE";
    private static final String SONG_ORDER_MODE = "SONG_ORDER_MODE";
    private static final String LAST_PLAY_SONG_ID = "LAST_PLAY_SONG_ID";
    private static final String RECENT_SONG_COUNT = "RECENT_SONG_COUNT";
    private static final int DEFAULT_RECENT_MAX_COUNT = 50;
    private static SharedPreferences sharedPreferences;

    private AppSetting() {
        //no instance
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
    public static long getLastPlaySongId() {
        return getSharedPreferences().getLong(LAST_PLAY_SONG_ID, 0);
    }

    /**
     * Sets last play song index.
     *
     * @param playSongId the play song index
     */
    public static void setLastPlaySongId(long playSongId) {
        getEdit().putLong(LAST_PLAY_SONG_ID, playSongId).apply();
    }

    /**
     * Gets recent song max count.
     *
     * @return the recent song max count
     */
    public static int getRecentSongMaxCount() {
        return getSharedPreferences().getInt(RECENT_SONG_COUNT, DEFAULT_RECENT_MAX_COUNT);
    }

    /**
     * Sets recent song max count.
     *
     * @param recentCount the recent count
     */
    public static void setRecentSongMaxCount(int recentCount) {
        putInt(RECENT_SONG_COUNT, recentCount);
    }

    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = MusicApplication.getContext().getSharedPreferences(APP_SETTING,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    private static void putInt(String key, int value) {
        getEdit().putInt(key, value).apply();
    }

    private static SharedPreferences.Editor getEdit() {
        return getSharedPreferences().edit();
    }

}
