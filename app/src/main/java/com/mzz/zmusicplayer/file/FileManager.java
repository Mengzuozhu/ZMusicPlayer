package com.mzz.zmusicplayer.file;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.song.SongInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author : Mzz
 * date : 2019 2019/4/25 18:57
 * description :
 */
public class FileManager {
    private static final String[] PROJECTION = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE
    };
    /**
     * 文件名中的歌手和歌名的分隔符
     */
    private static final String MINUS = " - ";
    private static FileManager mInstance = new FileManager();
    private static ContentResolver mContentResolver;

    public static FileManager getInstance() {
        mContentResolver = MusicApplication.getContext().getContentResolver();
        return mInstance;
    }

    /**
     * 获取本机音乐列表
     *
     * @return song infos
     */
    public List<SongInfo> getAllSongInfos(Set<Integer> allSongIdInFile) {
        ArrayList<SongInfo> songs = new ArrayList<>();
        try (Cursor cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {
            if (cursor == null) {
                return songs;
            }
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(4);
                if (isMusic == 0) {
                    continue;
                }

                int songIdInFile = cursor.getInt(5);
                String songPath = cursor.getString(0);
                boolean hasAddThisSong =
                        allSongIdInFile != null && allSongIdInFile.contains(songIdInFile);
                if (hasAddThisSong || isFileNotExit(songPath)) {
                    continue;
                }

                String displayName = cursor.getString(1);
                String artist = cursor.getString(2);
                String fileArtist = artist;
                int duration = cursor.getInt(3);
                //从文件名中提取歌名和歌手
                displayName = extractName(displayName);
                String[] strings = displayName.split(MINUS);
                if (strings.length > 1) {
                    artist = strings[0];
                    displayName = strings[1];
                }
                displayName = displayName.trim();
                artist = artist.trim();
                SongInfo song = new SongInfo();
                song.setName(displayName);
                song.setNameSpell(getUpperSpell(displayName));
                song.setPath(songPath);
                song.setArtist(artist);
                song.setFileArtist(fileArtist);
                song.setSongIdInFile(songIdInFile);
                song.setTitle(cursor.getString(6));
                song.setDuration(duration);
                song.setIsChecked(true);
                songs.add(song);
            }

        } catch (Exception e) {
            Log.e("FileManager", "getAllSongInfos fail", e);
        }
        return songs;
    }

    private boolean isFileNotExit(String path) {
        return !new File(path).exists();
    }

    private String getUpperSpell(String name) {
        String pinyin = Pinyin.toPinyin(name, "");
        return pinyin == null ? "" : pinyin.toUpperCase();
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
