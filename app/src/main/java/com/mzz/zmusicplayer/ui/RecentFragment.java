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
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@NoArgsConstructor
public class RecentFragment extends Fragment {

    @BindView(R.id.rv_recent_song)
    RecyclerView rvRecentSong;
    private Unbinder unbinder;
    private IPlayer player;
    private PlayList mPlayList;

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
        mPlayList = new PlayList();
        //需在创建视图后，重新初始化适配器
        initAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initAdapter();
        }
    }

    private void initAdapter() {
        if (rvRecentSong == null) {
            return;
        }
        List <SongInfo> recentSongs = getRecentSongs();
        mPlayList.setSongInfos(recentSongs);
        PlayListAdapter baseAdapter = new PlayListAdapter(mPlayList, rvRecentSong);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = baseAdapter.getItem(position);
            EventBus.getDefault().post(song);
            baseAdapter.updatePlaySongBackgroundColor(song);
        });
    }

    private List <SongInfo> getRecentSongs() {
        if (player == null) {
            player = Player.getInstance();
        }
        return player.getPlayList().getRecentSongs();
    }

}
