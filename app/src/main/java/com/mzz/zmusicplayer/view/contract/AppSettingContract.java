package com.mzz.zmusicplayer.view.contract;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author : Mzz
 * date : 2019 2019/6/10 15:22
 * description :
 */
public interface AppSettingContract {

    interface View {
        RecyclerView getRecyclerView();
    }

    interface Presenter {

        void initSetting();
    }
}
