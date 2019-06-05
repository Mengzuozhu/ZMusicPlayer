package com.mzz.zmusicplayer.ui;

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

import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.contract.LocalMusicContract;
import com.mzz.zmusicplayer.presenter.LocalMusicPresenter;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@NoArgsConstructor
public class LocalMusicFragment extends Fragment implements LocalMusicContract.View {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.fab_song_scroll_first)
    FloatingActionButton fabSongScrollFirst;
    private LocalMusicListener localMusicListener;
    private LocalMusicContract.Presenter mainPresenter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocalMusicFragment.
     */
    public static LocalMusicFragment newInstance() {
        return new LocalMusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        getListener();
        mainPresenter = new LocalMusicPresenter(this, localMusicListener);
        ViewerHelper.showOrHideScrollFirst(rvSong, mainPresenter.getLayoutManager(),
                fabSongScrollFirst);
    }

    private void getListener() {
        FragmentActivity activity = getActivity();
        if (activity instanceof LocalMusicListener) {
            localMusicListener = (LocalMusicListener) activity;
        }
    }

    public void addSongs(List <SongInfo> newSongInfos) {
        mainPresenter.addSongs(newSongInfos);
    }

    public void deleteByKeyInTx(List <Long> keys) {
        mainPresenter.deleteByKeyInTx(keys);
    }

    public void updateSongCountAndMode() {
        mainPresenter.updateSongCountAndMode();
    }

    public void updatePlaySongBackgroundColor(SongInfo song) {
        mainPresenter.updatePlaySongBackgroundColor(song);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvSong;
    }

    @OnClick(R.id.fab_song_scroll_first)
    public void scrollToFirstSongOnClick() {
        mainPresenter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick() {
        mainPresenter.locateToSelectedSong();
    }

    public interface LocalMusicListener {

        void setPlayList(PlayList playList);

        void setPlayingIndex(int playingIndex);
    }
}
