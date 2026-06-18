package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mzz.zmusicplayer.databinding.FragmentSongListBinding;
import com.mzz.zmusicplayer.enums.SongListType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LocalSongFragment extends SongFragment {

    private FragmentSongListBinding binding;
    private SongListAdapter songListAdapter;
    private LocalSong localSongs;
    private boolean isVisibleToUser;

    public static LocalSongFragment newInstance() {
        return new LocalSongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        localSongs = LocalSong.getInstance();
        binding.fabScrollFirstSong.setOnClickListener(v -> songListAdapter.scrollToFirst());
        binding.fabSongLocate.setOnClickListener(v -> songListAdapter.locateToSelectedSong());
        init();
        return binding.getRoot();
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
        binding = null;
        isVisibleToUser = false;
    }

    public void addToLocalSongs(Collection<SongInfo> songs) {
        songListAdapter.updateData(localSongs.addToLocalSongs(songs));
    }

    @Override
    public void remove(List<Long> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        List<Long> deleteIds = new ArrayList<>(keys);
        songListAdapter.updateData(localSongs.remove(deleteIds));
        Player.getInstance().removeSongsFromPlayList(deleteIds);
    }

    @Override
    public void removeSong(SongInfo song) {
        if (song == null) {
            return;
        }
        localSongs.remove(song);
        songListAdapter.removeSong(song);
        Player.getInstance().removeSongsFromPlayList(Collections.singletonList(song.getId()));
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        if (!isVisibleToUser) {
            return;
        }
        songListAdapter.updatePlaySongBackgroundColor(song);
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
        if (binding == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.LOCAL), binding.rvSong, getActivity()) {
            @Override
            public void removeSongAt(int position) {
                SongInfo song = getItem(position);
                localSongs.remove(song);
                super.removeSongAt(position);
                if (song != null) {
                    Player.getInstance().removeSongsFromPlayList(Collections.singletonList(song.getId()));
                }
            }
        };
        songListAdapter.setScrollFirstShowInNeed(binding.fabScrollFirstSong);
    }

}
