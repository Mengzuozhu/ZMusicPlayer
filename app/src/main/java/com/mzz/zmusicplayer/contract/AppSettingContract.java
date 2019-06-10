package com.mzz.zmusicplayer.contract;

import android.support.v7.widget.RecyclerView;

/**
 * author : Mzz
 * date : 2019 2019/6/10 15:22
 * description :
 */
public interface AppSettingContract {

    interface View {
        RecyclerView getRecyclerView();

        void showCityPickerActivity();
    }

    interface Presenter {

        void initSetting();
    }
}
