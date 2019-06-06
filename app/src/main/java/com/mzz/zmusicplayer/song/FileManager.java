package com.mzz.zmusicplayer.song;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;

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
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE
    };
    //文件名中的歌手和歌名的分隔符
    private static final String MINUS = " - ";
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
                int isMusic = c.getInt(4);
                if (isMusic == 0 || !new File(path).exists()) {
                    continue;
                }

                String name = c.getString(1); // DISPLAY_NAME
                String artist = c.getString(2);
                String fileArtist = artist;
                int duration = c.getInt(3);
                name = extractName(name);
                String[] strings = name.split(MINUS);
                if (strings.length > 1) {
                    artist = strings[0];
                    name = strings[1];
                }
                name = name.trim();
                artist = artist.trim();
                SongInfo song = new SongInfo();
                song.setName(name);
                song.setNameSpell(getUpperSpell(name));
                song.setPath(path);
                song.setArtist(artist);
                song.setFileArtist(fileArtist);
                song.setFileId(c.getInt(5));
                song.setTitle(c.getString(6));
                song.setDuration(duration);
                song.setIsChecked(true);
                songs.add(song);
            }

        } catch (Exception e) {
            Log.d("FileManager", "e:" + e.getMessage());
        }
        return songs;
    }

    private String getUpperSpell(String name) {
        String pinyin = Pinyin.toPinyin(name, "");
        if (pinyin == null) {
            pinyin = "";
        }
        return pinyin.toUpperCase();
    }

    private String extractName(String name) {
        int bracketIndex = name.lastIndexOf('[');
        if (bracketIndex > 0) {
            name = name.substring(0, bracketIndex);
        }

        int dotIndex = name.lastIndexOf('.');
        //去除后缀
        if (dotIndex > 0) {
            name = name.substring(0, dotIndex);
        }
        return name;
    }

}
