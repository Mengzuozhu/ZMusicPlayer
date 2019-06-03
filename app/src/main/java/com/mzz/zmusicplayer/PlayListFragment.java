package com.mzz.zmusicplayer;

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
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.presenter.MainPresenter;
import com.mzz.zmusicplayer.song.PlayListener;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayListFragment extends Fragment implements MainContract.View {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.fab_song_scroll_first)
    FloatingActionButton fabSongScrollFirst;
    PlayListener playListener;
    @Getter
    private MainContract.Presenter mainPresenter;

    public PlayListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayListFragment.
     */
    public static PlayListFragment newInstance() {
        PlayListFragment fragment = new PlayListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayListener) {
            playListener = (PlayListener) activity;
        }
        mainPresenter = new MainPresenter(this, playListener);
        playListener.setMainPresenter(mainPresenter);
        ViewerHelper.showOrHideScrollFirst(rvSong, mainPresenter.getLayoutManager(),
                fabSongScrollFirst);
    }

    public void addSongs(List <SongInfo> newSongInfos) {
        mainPresenter.addSongs(newSongInfos);
    }

    public void deleteByKeyInTx(Iterable <Long> keys) {
        mainPresenter.deleteByKeyInTx(keys);
    }

    public void updateSongCountAndMode() {
        mainPresenter.updateSongCountAndMode();
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

}
