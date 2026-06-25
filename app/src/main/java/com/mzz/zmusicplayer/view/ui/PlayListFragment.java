package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zmusicplayer.databinding.FragmentSongListBinding;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.contract.PlayListContract;
import com.mzz.zmusicplayer.view.presenter.PlayListPresenter;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayListFragment extends SongFragment implements PlayListContract.View {

    private FragmentSongListBinding binding;
    private PlayListListener playListListener;
    private PlayListContract.Presenter playListPresenter;

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabScrollFirstSong.setOnClickListener(v -> playListPresenter.scrollToFirst());
        binding.fabSongLocate.setOnClickListener(v -> playListPresenter.locateToSelectedSong());
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updatePlayListSongs(List<SongInfo> checkedSongs) {
        playListPresenter.updatePlayListSongs(checkedSongs);
    }

    @Override
    public void remove(List<Long> keys) {
        playListPresenter.remove(keys);
    }

    @Override
    public void removeSong(SongInfo song) {
        playListPresenter.remove(song);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.rvSong;
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {

    }

    private void init() {
        getListener();
        playListPresenter = new PlayListPresenter(this, playListListener);
        playListPresenter.setScrollFirstShowInNeed(binding.fabScrollFirstSong);
        playListPresenter.locateToSelectedSong();
    }

    private void getListener() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayListListener) {
            playListListener = (PlayListListener) activity;
        }
    }

    public interface PlayListListener {

        void setPlayList(PlayList playList);

    }
}
