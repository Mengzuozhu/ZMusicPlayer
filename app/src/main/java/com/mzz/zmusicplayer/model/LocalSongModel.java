package com.mzz.zmusicplayer.model;

import android.content.Context;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.UpgradeDbHelper;
import com.mzz.zmusicplayer.greendao.db.DaoMaster;
import com.mzz.zmusicplayer.greendao.db.DaoSession;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/29 9:10
 * description :
 */
public class LocalSongModel {
    private static SongInfoDao songInfoDao;

    static {
        Context context = MusicApplication.getContext();
        UpgradeDbHelper helper = new UpgradeDbHelper(context,
                context.getString(R.string.db_name_local_song));
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        songInfoDao = daoSession.getSongInfoDao();
    }

    private LocalSongModel() {
        //no instance
    }

    /**
     * Gets order local playSongs.
     *
     * @return the order local playSongs
     */
    public static List<SongInfo> getOrderLocalSongs() {
        List<SongInfo> songInfos = songInfoDao.loadAll();
        sort(songInfos);
        return songInfos;
    }

    private static void sort(List<SongInfo> songInfos) {
        switch (AppSetting.getSongSortMode()) {
            case ORDER_ASCEND_BY_NAME:
                LocalSong.sortByChineseName(songInfos, true);
                break;
            case ORDER_DESCEND_BY_NAME:
                LocalSong.sortByChineseName(songInfos, false);
                break;
            default:
                LocalSong.sortById(songInfos);
                break;
        }
    }

    /**
     * Delete by key in tx.
     *
     * @param keys the keys
     */
    public static void deleteByKeyInTx(Iterable<Long> keys) {
        songInfoDao.deleteByKeyInTx(keys);
    }

    /**
     * Delete.
     *
     * @param song the song
     */
    public static void delete(SongInfo song) {
        if (song == null) {
            return;
        }
        songInfoDao.delete(song);
    }

    /**
     * Insert or replace in tx.
     *
     * @param songInfos the song infos
     */
    public static void insertOrReplaceInTx(Iterable<SongInfo> songInfos) {
        if (songInfos == null) {
            return;
        }
        songInfoDao.insertOrReplaceInTx(songInfos);
    }

    /**
     * Update.
     *
     * @param songInfo the song info
     */
    public static void update(SongInfo songInfo) {
        if (songInfo == null) {
            return;
        }
        songInfoDao.update(songInfo);
    }

    /**
     * Update in tx.
     *
     * @param songs the songs
     */
    public static void updateInTx(Iterable<SongInfo> songs) {
        if (songs == null) {
            return;
        }
        songInfoDao.updateInTx(songs);
    }

}
