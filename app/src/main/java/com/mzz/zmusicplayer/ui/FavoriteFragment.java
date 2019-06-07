package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.header.SongListHeader;
import com.mzz.zmusicplayer.model.PlayListModel;
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
 */
@NoArgsConstructor
public class FavoriteFragment extends Fragment {

    @BindView(R.id.rv_favorite_song)
    RecyclerView rvFavoriteSong;
    Unbinder unbinder;
    private SongListHeader songListHeader;
    private IPlayer player;
    private PlayList mPlayList;

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
        player = Player.getInstance();
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
        List <SongInfo> favoriteSongs = player.getPlayList().getFavoriteSongs();
        mPlayList.setPlaySongs(favoriteSongs);
        PlayListAdapter playListAdapter = new PlayListAdapter(mPlayList, rvFavoriteSong) {
            @Override
            public void removeSongAt(int position) {
                SongInfo songInfo = this.getItem(position);
                if (songInfo == null) {
                    return;
                }
                if (songInfo.isPlayListSelected()) {
                    player.switchFavorite();
                } else {
                    songInfo.setIsFavorite(false);
                    PlayListModel.update(songInfo);
                }
                super.removeSongAt(position);
                songListHeader.updateSongCount();
            }
        };
        playListAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = playListAdapter.getItem(position);
            playListAdapter.updatePlaySongBackgroundColor(song);
            EventBus.getDefault().post(song);
        });
        songListHeader = new SongListHeader(getActivity(), playListAdapter);
    }

}
