package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.contract.PlayListContract;
import com.mzz.zmusicplayer.view.presenter.PlayListPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@NoArgsConstructor
public class PlayListFragment extends Fragment implements PlayListContract.View {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.fab_scroll_first_song)
    FloatingActionButton fabSongScrollFirst;
    private PlayListListener playListListener;
    private PlayListContract.Presenter playListPresenter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayListFragment.
     */
    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        getListener();
        playListPresenter = new PlayListPresenter(this, playListListener);
        playListPresenter.setScrollFirstShowInNeed(fabSongScrollFirst);
    }

    private void getListener() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayListListener) {
            playListListener = (PlayListListener) activity;
        }
    }

    /**
     * Update play list songs.
     *
     * @param checkedSongs the checked songs
     */
    public void updatePlayListSongs(List<SongInfo> checkedSongs) {
        playListPresenter.updatePlayListSongs(checkedSongs);
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    public void remove(List<Long> keys) {
        playListPresenter.remove(keys);
    }

    public void remove(SongInfo song) {
        playListPresenter.remove(song);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvSong;
    }

    @OnClick(R.id.fab_scroll_first_song)
    public void scrollToFirstSongOnClick() {
        playListPresenter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick() {
        playListPresenter.locateToSelectedSong();
    }

    public interface PlayListListener {

        void setPlayList(PlayList playList);

        void setPlayingIndex(int playingIndex);
    }
}
