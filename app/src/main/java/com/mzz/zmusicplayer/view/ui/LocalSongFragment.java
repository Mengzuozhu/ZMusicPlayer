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
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongListAdapter;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author Mengzz
 */
@NoArgsConstructor
public class LocalSongFragment extends SongFragment {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.fab_scroll_first_song)
    FloatingActionButton fabSongScrollFirst;
    private Unbinder unbinder;
    private SongListAdapter songListAdapter;
    private LocalSong localSongs;
    private boolean isVisibleToUser;

    /**
     * New instance favorite fragment.
     *
     * @return the favorite fragment
     */
    public static LocalSongFragment newInstance() {
        return new LocalSongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        localSongs = LocalSong.getInstance();
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isVisibleToUser) {
            init();
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
     * Add to local.
     *
     * @param songs the songs
     */
    public void addToLocalSongs(Collection<SongInfo> songs) {
        songListAdapter.updateData(localSongs.addToLocalSongs(songs));
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    @Override
    public void remove(List<Long> keys) {
        songListAdapter.updateData(localSongs.remove(keys));
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
        localSongs.remove(song);
        songListAdapter.removeSong(song);
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        if (!isVisibleToUser) {
            return;
        }
        songListAdapter.updatePlaySongBackgroundColor(song);
    }

    @OnClick(R.id.fab_scroll_first_song)
    public void scrollToFirstSongOnClick() {
        songListAdapter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick() {
        songListAdapter.locateToSelectedSong();
    }

    private void init() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List<SongInfo> allLocalSongs = localSongs.getAllLocalSongs();
            songListAdapter.updateData(allLocalSongs);
        }
    }

    private void initAdapter() {
        if (rvSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.LOCAL), rvSong, getActivity()) {
            @Override
            public void removeSongAt(int position) {
                localSongs.remove(getItem(position));
                super.removeSongAt(position);
            }
        };
        songListAdapter.setScrollFirstShowInNeed(fabSongScrollFirst);
    }

}
