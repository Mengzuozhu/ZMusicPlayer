package com.mzz.zmusicplayer.model;

import android.content.Context;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.UpgradeDbHelper;
import com.mzz.zmusicplayer.greendao.db.DaoMaster;
import com.mzz.zmusicplayer.greendao.db.DaoSession;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/29 9:10
 * description :
 */
public class PlayListModel {
    private static SongInfoDao songInfoDao;

    static {
        Context context = MusicApplication.getContext();
        UpgradeDbHelper helper = new UpgradeDbHelper(context,
                context.getString(R.string.play_list_db_name));
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        songInfoDao = daoSession.getSongInfoDao();
    }

    /**
     * Gets order song infos.
     *
     * @return the order song infos
     */
    public static List <SongInfo> getOrderLocalSongs() {
        List <SongInfo> songInfos = loadAll();
        sort(songInfos);
        return songInfos;
    }

    public static List <SongInfo> getOrderPlayListSongs() {
        List <SongInfo> songInfos = loadAll();
        switch (AppSetting.getPlayListType()) {
            case RECENT:
                return PlayList.getRecentSongs(songInfos);
            case FAVORITE:
                songInfos = PlayList.getFavoriteSongs(songInfos);
                break;
            default:
                break;
        }
        sort(songInfos);
        return songInfos;
    }

    private static void sort(List <SongInfo> songInfos) {
        switch (AppSetting.getSongSortMode()) {
            case ORDER_ASCEND_BY_NAME:
                PlayList.sortByChineseName(songInfos, true);
                break;
            case ORDER_DESCEND_BY_NAME:
                PlayList.sortByChineseName(songInfos, false);
                break;
            default:
                PlayList.sortById(songInfos);
                break;
        }
    }

    private static List <SongInfo> loadAll() {
        return songInfoDao.loadAll();
    }

    /**
     * Delete by key in tx.
     *
     * @param keys the keys
     */
    public static void deleteByKeyInTx(Iterable <Long> keys) {
        songInfoDao.deleteByKeyInTx(keys);
    }

    public static void delete(SongInfo song) {
        if (song == null) {
            return;
        }
        songInfoDao.delete(song);
    }

    public static void insertOrReplaceInTx(Iterable <SongInfo> songInfos) {
        if (songInfos == null) {
            return;
        }
        songInfoDao.insertOrReplaceInTx(songInfos);
    }

    public static void update(SongInfo songInfo) {
        if (songInfo == null) {
            return;
        }
        songInfoDao.update(songInfo);
    }

}
