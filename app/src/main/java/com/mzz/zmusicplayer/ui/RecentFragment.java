package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.song.IPlayer;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFragment extends Fragment {

    @BindView(R.id.rv_recent_song)
    RecyclerView rvRecentSong;
    private Unbinder unbinder;
    private IPlayer player;
    private PlayList mPlayList;
    private PlayListAdapter baseAdapter;

    public RecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecentFragment.
     */
    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updatePlayList();
        }
    }

    private void updatePlayList() {
        if (rvRecentSong == null) {
            return;
        }
        if (mPlayList == null) {
            mPlayList = new PlayList();
        }

        List <SongInfo> recentSongs = getRecentSongs();
        mPlayList.setSongInfos(recentSongs);
        if (baseAdapter == null) {
            baseAdapter = new PlayListAdapter(mPlayList, rvRecentSong, false);
            baseAdapter.setOnItemClickListener((adapter, view, position) -> {
                SongInfo song = baseAdapter.getItem(position);
                EventBus.getDefault().post(song);
                baseAdapter.updatePlaySongBackgroundColor(song);
            });
        } else {
            baseAdapter.setNewData(recentSongs);
        }
    }

    private List <SongInfo> getRecentSongs() {
        if (player == null) {
            player = Player.getInstance();
        }
        return player.getPlayList().getRecentSongs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
