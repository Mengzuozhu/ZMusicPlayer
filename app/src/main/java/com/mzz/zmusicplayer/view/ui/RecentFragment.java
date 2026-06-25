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
import com.mzz.zmusicplayer.song.RecentSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongListAdapter;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecentFragment extends SongFragment {

    private FragmentSongListBinding binding;
    private SongListAdapter songListAdapter;
    private RecentSong recentSong;
    private boolean isVisibleToUser;

    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        recentSong = RecentSong.getInstance();
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

    @Override
    public void remove(List<Long> keys) {
        songListAdapter.updateData(recentSong.remove(keys));
    }

    @Override
    public void removeSong(SongInfo song) {
        if (song == null) {
            return;
        }
        recentSong.remove(song);
        songListAdapter.removeSong(song);
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
            List<SongInfo> recentSongs = recentSong.getRecentSongs();
            songListAdapter.updateData(recentSongs);
        }
    }

    private void initAdapter() {
        if (binding == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.RECENT), binding.rvSong,
                getActivity()) {
            @Override
            public void removeSongAt(int position) {
                recentSong.remove(this.getItem(position));
                super.removeSongAt(position);
            }
        };
        songListAdapter.setScrollFirstShowInNeed(binding.fabScrollFirstSong);
    }

}
