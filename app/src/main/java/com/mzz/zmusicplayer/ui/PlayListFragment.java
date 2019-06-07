package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.contract.PlayListContract;
import com.mzz.zmusicplayer.presenter.PlayListPresenter;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

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
    @BindView(R.id.fab_song_scroll_first)
    FloatingActionButton fabSongScrollFirst;
    private PlayListListener playListListener;
    private PlayListContract.Presenter mainPresenter;

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
        mainPresenter = new PlayListPresenter(this, playListListener);
        LinearLayoutManager layoutManager = mainPresenter.getLayoutManager();
        rvSong.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager != null) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if (position > 0) {
                        View view = fabSongScrollFirst;
                        view.setVisibility(View.VISIBLE);
                    } else if (fabSongScrollFirst.isShown()) {
                        View view = fabSongScrollFirst;
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void getListener() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayListListener) {
            playListListener = (PlayListListener) activity;
        }
    }

    public void updatePlayListSongs(List <SongInfo> checkedSongs){
        mainPresenter.updatePlayListSongs(checkedSongs);
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

    public interface PlayListListener {

        void setPlayList(PlayList playList);

        void setPlayingIndex(int playingIndex);
    }
}
