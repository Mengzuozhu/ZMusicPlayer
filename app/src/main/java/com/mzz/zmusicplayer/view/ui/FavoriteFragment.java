package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.enums.SongListType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.FavoriteSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class FavoriteFragment extends SongFragment implements FavoriteSong.IFavoriteSongObserver {

    @BindView(R.id.rv_song)
    RecyclerView rvFavoriteSong;
    @BindView(R.id.fab_scroll_first_song)
    FloatingActionButton fabSongScrollFirst;
    private Unbinder unbinder;
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
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        favoriteSong = FavoriteSong.getInstance();
        favoriteSong.setFavoriteSongObserver(this);
        initOrUpdate();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isVisibleToUser) {
            initOrUpdate();
            isVisibleToUser = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        isVisibleToUser = false;
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    @Override
    public void remove(List<Long> keys) {
        songListAdapter.updateData(favoriteSong.remove(keys));
    }

    /**
     * Remove song.
     *
     * @param song the song
     */
    @Override
    public void removeSong(SongInfo song) {
        if (song == null) {
            return;
        }
        //获取喜欢列表中的对应歌曲，保证删除的是指定对象
        int songIndexById = PlayList.getSongIndexById(songListAdapter.getData(), song.getId());
        favoriteSong.remove(songListAdapter.getItem(songIndexById));
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

    @OnClick(R.id.fab_scroll_first_song)
    public void scrollToFirstSongOnClick() {
        songListAdapter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick() {
        songListAdapter.locateToSelectedSong();
    }

    private void initOrUpdate() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List<SongInfo> favoriteSongs = favoriteSong.getFavoriteSongs();
            songListAdapter.updateData(favoriteSongs);
        }
    }

    private void initAdapter() {
        if (rvFavoriteSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.FAVORITE), rvFavoriteSong
                , getActivity()) {
            @Override
            public void removeSongAt(int position) {
                favoriteSong.remove(getItem(position));
            }
        };
        songListAdapter.setScrollFirstShowInNeed(fabSongScrollFirst);
    }

}
