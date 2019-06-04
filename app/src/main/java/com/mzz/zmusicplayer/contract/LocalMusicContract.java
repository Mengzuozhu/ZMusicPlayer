package com.mzz.zmusicplayer.contract;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public interface LocalMusicContract {

    interface View {

        FragmentActivity getActivity();

        RecyclerView getRecyclerView();

    }

    interface Presenter {

        void updatePlaySongBackgroundColor(SongInfo song);

        LinearLayoutManager getLayoutManager();

        void addSongs(List <SongInfo> songInfos);

        void scrollToFirst();

        void locateToSelectedSong();

        void updateSongCountAndMode();

        void deleteByKeyInTx(List <Long> keys);

        void finishMainActivity();
    }
}
