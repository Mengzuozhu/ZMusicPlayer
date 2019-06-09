package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongListAdapter;
import com.mzz.zmusicplayer.edit.EditType;
import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.song.IPlayer;
import com.mzz.zmusicplayer.song.LocalSongClass;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

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
    private SongListAdapter songListAdapter;
    private LocalSongClass localSongs;
    private IPlayer player;

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
        player = Player.getInstance();
        localSongs = LocalSongClass.getInstance();
        init();
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
            init();
        }
    }

    private void init() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List <SongInfo> favoriteSongs = localSongs.getFavoriteSongs();
            songListAdapter.updateData(favoriteSongs);
        }
    }

    private void initAdapter() {
        if (rvFavoriteSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(), rvFavoriteSong, getActivity(),
                EditType.FAVORITE) {
            @Override
            public void removeSongAt(int position) {
                SongInfo song = this.getItem(position);
                if (song == null) {
                    return;
                }
                if (song.isPlayListSelected()) {
                    player.switchFavorite();
                } else {
                    song.setIsFavorite(false);
                    LocalSongModel.update(song);
                }
                super.removeSongAt(position);
                updateSongCount();
            }
        };
    }

}
