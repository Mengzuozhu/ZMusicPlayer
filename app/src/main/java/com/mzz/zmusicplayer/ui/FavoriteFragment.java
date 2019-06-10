package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongListAdapter;
import com.mzz.zmusicplayer.edit.EditType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.FavoriteSong;
import com.mzz.zmusicplayer.song.ISongChangeListener;
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
public class FavoriteFragment extends Fragment implements ISongChangeListener,
        FavoriteSong.IFavoriteSongObserver {

    @BindView(R.id.rv_favorite_song)
    RecyclerView rvFavoriteSong;
    Unbinder unbinder;
    private FavoriteSong favoriteSong;
    private SongListAdapter songListAdapter;
    private boolean isVisibleToUser;

    /**
     * New instance favorite fragment.
     *
     * @return the favorite fragment
     */
    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        unbinder = ButterKnife.bind(this, view);
        favoriteSong = FavoriteSong.getInstance();
        favoriteSong.setFavoriteSongObserver(this);
        initOrUpdate();
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
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            initOrUpdate();
        }
    }

    private void initOrUpdate() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List <SongInfo> favoriteSongs = favoriteSong.getFavoriteSongs();
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
                favoriteSong.remove(getItem(position));
            }
        };
    }

    public void remove(List <Long> keys) {
        songListAdapter.updateData(favoriteSong.remove(keys));
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        if (!isVisibleToUser) {
            return;
        }
        songListAdapter.updatePlaySongBackgroundColor(song);
    }

    @Override
    public void onFavoriteSongChange() {
        initOrUpdate();
    }
}
