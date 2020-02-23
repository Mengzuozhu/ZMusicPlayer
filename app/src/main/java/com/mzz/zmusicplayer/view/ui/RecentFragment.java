package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.song.ISongChangeListener;
import com.mzz.zmusicplayer.song.RecentSong;
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
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@NoArgsConstructor
public class RecentFragment extends Fragment implements ISongChangeListener {

    @BindView(R.id.rv_song)
    RecyclerView rvRecentSong;
    @BindView(R.id.fab_scroll_first_song)
    FloatingActionButton fabSongScrollFirst;
    private Unbinder unbinder;
    private SongListAdapter songListAdapter;
    private RecentSong recentSong;
    private boolean isVisibleToUser;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecentFragment.
     */
    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        recentSong = RecentSong.getInstance();
        //需在创建视图后，重新初始化适配器
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            init();
        }
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
        if (rvRecentSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(SongListType.RECENT), rvRecentSong,
                getActivity()) {
            @Override
            public void removeSongAt(int position) {
                recentSong.remove(this.getItem(position));
                super.removeSongAt(position);
            }
        };
        songListAdapter.setScrollFirstShowInNeed(fabSongScrollFirst);
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    public void remove(List<Long> keys) {
        songListAdapter.updateData(recentSong.remove(keys));
    }

    /**
     * Remove song.
     *
     * @param song the song
     */
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

    @OnClick(R.id.fab_scroll_first_song)
    public void scrollToFirstSongOnClick() {
        songListAdapter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick() {
        songListAdapter.locateToSelectedSong();
    }

}
