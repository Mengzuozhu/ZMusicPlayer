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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    @BindView(R.id.rv_favorite_song)
    RecyclerView rvFavoriteSong;
    Unbinder unbinder;
    private IPlayer player;
    private PlayList mPlayList;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * New instance favorite fragment.
     *
     * @return the favorite fragment
     */
    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPlayList = new PlayList();
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
        if (rvFavoriteSong == null) {
            return;
        }
        List <SongInfo> favoriteSongs = getFavoriteSongs();
        mPlayList.setSongInfos(favoriteSongs);
        PlayListAdapter baseAdapter = new PlayListAdapter(mPlayList, rvFavoriteSong);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = baseAdapter.getItem(position);
            baseAdapter.updatePlaySongBackgroundColor(song);
            EventBus.getDefault().post(song);
        });
    }

    private List <SongInfo> getFavoriteSongs() {
        if (player == null) {
            player = Player.getInstance();
        }
        PlayList playList = player.getPlayList();
        List <SongInfo> favoriteSongs = new ArrayList <>();
        List <SongInfo> songInfos = playList.getSongInfos();
        for (SongInfo songInfo : songInfos) {
            if (songInfo.getIsFavorite()) {
                favoriteSongs.add(songInfo);
            }
        }
        return favoriteSongs;
    }

}