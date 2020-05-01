package com.mzz.zmusicplayer.view.contract;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
