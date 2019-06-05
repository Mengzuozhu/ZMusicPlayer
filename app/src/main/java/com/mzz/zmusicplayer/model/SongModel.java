package com.mzz.zmusicplayer.model;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.greendao.db.DaoSession;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/29 9:10
 * description :
 */
public class SongModel {
    private static DaoSession daoSession;
    private static SongInfoDao songInfoDao;

    static {
        daoSession = MusicApplication.getDaoSession();
        songInfoDao = daoSession.getSongInfoDao();
    }

    /**
     * Gets order song infos.
     *
     * @return the order song infos
     */
    public static List <SongInfo> getOrderSongInfos() {
        List <SongInfo> songInfos = loadAll();
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
        return songInfos;
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

    public static List <Long> integerToLongList(List <Integer> deleteNum) {
        ArrayList <Long> ids = new ArrayList <>();
        for (Integer i : deleteNum) {
            ids.add(i.longValue());
        }
        return ids;
    }

    public static SongInfo getSongInfoById(Object key) {
        return daoSession.load(SongInfo.class, key);
    }

}
