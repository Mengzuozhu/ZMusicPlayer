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
import com.mzz.zmusicplayer.song.FavoriteSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongListAdapter;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FavoriteFragment extends SongFragment implements FavoriteSong.IFavoriteSongObserver {

    private FragmentSongListBinding binding;
    private FavoriteSong favoriteSong;
    private SongListAdapter songListAdapter;
    private boolean isVisibleToUser;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        favoriteSong = FavoriteSong.getInstance();
        favoriteSong.setFavoriteSongObserver(this);
        binding.fabScrollFirstSong.setOnClickListener(v -> songListAdapter.scrollToFirst());
        binding.fabSongLocate.setOnClickListener(v -> songListAdapter.locateToSelectedSong());
        initOrUpdate();
        return binding.getRoot();
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
        binding = null;
        isVisibleToUser = false;
    }

    @Override
    public void remove(List<Long> keys) {
        songListAdapter.updateData(favoriteSong.remove(keys));
    }

    @Override
    public void removeSong(SongInfo song) {
        if (song == null) {
            return;
        }
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

    private void initOrUpdate() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List<SongInfo> favoriteSongs = favoriteSong.getFavoriteSongs();
            songListAdapter.updateData(favoriteSongs);
        }
    }

    private void initAdapter() {
        if (binding == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.FAVORITE), binding.rvSong
                , getActivity()) {
            @Override
            public void removeSongAt(int position) {
                favoriteSong.remove(getItem(position));
            }
        };
        songListAdapter.setScrollFirstShowInNeed(binding.fabScrollFirstSong);
    }

}
