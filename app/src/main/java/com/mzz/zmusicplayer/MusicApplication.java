package com.mzz.zmusicplayer;

import android.app.Application;

import com.mzz.zmusicplayer.common.UpgradeDbHelper;
import com.mzz.zmusicplayer.greendao.db.DaoMaster;
import com.mzz.zmusicplayer.greendao.db.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * author : Mzz
 * date : 2019 2019/4/26 21:22
 * description :
 */
public class MusicApplication extends Application {
    private static DaoSession daoSession;
    private static MusicApplication sInstance;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static MusicApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        UpgradeDbHelper helper = new UpgradeDbHelper(this,
                this.getString(R.string.song_db_name));
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }
}
