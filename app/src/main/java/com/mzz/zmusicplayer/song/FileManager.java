package com.mzz.zmusicplayer.song;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/4/25 18:57
 * description :
 */
public class FileManager {
    private static final String[] projection = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM};
    private static FileManager mInstance = new FileManager();
    private static ContentResolver mContentResolver;

    public static FileManager getInstance(Context context) {
        mContentResolver = context.getContentResolver();
        return mInstance;
    }

    /**
     * 获取本机音乐列表
     *
     * @return song infos
     */
    public List <SongInfo> getSongInfos() {
        ArrayList <SongInfo> songs = new ArrayList <>();
        try (Cursor c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {
            if (c == null) {
                return songs;
            }
            while (c.moveToNext()) {
                // 路径
                String path = c.getString(0);
                int isMusic = c.getInt(5);
                if (isMusic == 0 || !new File(path).exists()) {
                    continue;
                }

                String name = c.getString(1); // DISPLAY_NAME
                String title = c.getString(2); // 歌曲名
                String artist = c.getString(3);
                int duration = c.getInt(4);
                name = extractName(name);
                SongInfo song = new SongInfo(name, path, artist, duration, false);
                songs.add(song);
            }

        } catch (Exception e) {
            Log.d("FileManager", "e:" + e.getMessage());
        }
        return songs;
    }

    private String extractName(String name) {
        int dotIndex = name.indexOf('.');
        if (dotIndex > 0) {
            name = name.substring(0, dotIndex);
        }

        int bracketIndex = name.indexOf('[');
        if (bracketIndex > 0) {
            name = name.substring(0, bracketIndex);
        }

        String s = "-";
        if (name.contains(s)) {
            String[] strings = name.split(s);
            if (strings.length > 1) {
                name = strings[1];
            }
        }
        return name;
    }

}
