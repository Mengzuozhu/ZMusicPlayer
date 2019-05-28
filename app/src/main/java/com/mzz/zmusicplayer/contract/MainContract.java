package com.mzz.zmusicplayer.contract;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.mzz.zmusicplayer.song.SongInfo;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public interface MainContract {

    interface View {
        FragmentActivity getActivity();

        void setControlFragment(SongInfo songInfo);

        RecyclerView getRecyclerView();

    }

    interface Presenter {
    }
}
