package com.mzz.zmusicplayer.contract;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * @author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public interface PlayListContract {

    interface View {

        FragmentActivity getActivity();

        RecyclerView getRecyclerView();

    }

    interface Presenter {

        void setScrollFirstShowInNeed(FloatingActionButton floatingActionButton);

        void updatePlayListSongs(List<SongInfo> songInfos);

        void scrollToFirst();

        void locateToSelectedSong();

        void remove(List<Long> keys);

        void remove(SongInfo song);

    }
}
