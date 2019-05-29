package com.mzz.zmusicplayer.model;

import com.mzz.zmusicplayer.MyApplication;
import com.mzz.zmusicplayer.greendao.db.DaoSession;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;
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
        daoSession = MyApplication.getDaoSession();
        songInfoDao = daoSession.getSongInfoDao();
    }

    public static List <SongInfo> getSortedSongInfos() {
        List <SongInfo> data = daoSession.loadAll(SongInfo.class);
        data.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        return data;
    }

    public static void deleteByKey(Iterable <Long> keys) {
        songInfoDao.deleteByKeyInTx(keys);
    }

    public static void insertOrReplace(SongInfo songInfo) {
        if (songInfo == null) {
            return;
        }
        songInfoDao.insertOrReplace(songInfo);
    }

    public static void insertOrReplaceInTx(Iterable<SongInfo> songInfos) {
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
