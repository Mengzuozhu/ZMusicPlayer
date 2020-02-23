package com.mzz.zmusicplayer.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mzz.zandroidcommon.common.MigrationDbHelper;
import com.mzz.zmusicplayer.greendao.db.DaoMaster;
import com.mzz.zmusicplayer.greendao.db.SongInfoDao;

/**
 * 升级greendao数据库
 * @author : Mzz
 * date : 2019 2019/5/18 15:41
 * description :
 */
public class UpgradeDbHelper extends DaoMaster.OpenHelper {

    public UpgradeDbHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            MigrationDbHelper.migrate(db, SongInfoDao.class);
        }
    }
}
