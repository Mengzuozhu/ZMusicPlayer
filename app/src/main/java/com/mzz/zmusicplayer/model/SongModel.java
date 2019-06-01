package com.mzz.zmusicplayer.model;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.greendao.db.DaoSession;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;
import com.mzz.zmusicplayer.setting.AppSetting;
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

    public static List <SongInfo> getOrderSongInfos() {
        switch (AppSetting.getSongSortMode()) {
            case ORDER_ASCEND_BY_NAME:
                return songInfoDao.queryBuilder().orderAsc(SongInfoDao.Properties.Name).list();
            case ORDER_DESCEND_BY_NAME:
                return songInfoDao.queryBuilder().orderDesc(SongInfoDao.Properties.Name).list();
            default:
                return songInfoDao.queryBuilder().orderAsc(SongInfoDao.Properties.Id).list();
        }
    }

    private static List <SongInfo> loadAll() {
        return daoSession.loadAll(SongInfo.class);
    }

    public static void deleteByKey(Iterable <Long> keys) {
        songInfoDao.deleteByKeyInTx(keys);
    }

    public static void insertOrReplaceInTx(Iterable <SongInfo> songInfos) {
        if (songInfos == null) {
            return;
        }
        songInfoDao.insertOrReplaceInTx(songInfos);
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
