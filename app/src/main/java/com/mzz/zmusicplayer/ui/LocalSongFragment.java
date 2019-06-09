package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.edit.EditType;
import com.mzz.zmusicplayer.header.SongListHeader;
import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.song.IPlayer;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class LocalSongFragment extends Fragment {

    @BindView(R.id.rv_local_song)
    RecyclerView rvLocalSong;
    Unbinder unbinder;
    private PlayListAdapter playListAdapter;
    private SongListHeader songListHeader;
    private IPlayer player;

    /**
     * New instance favorite fragment.
     *
     * @return the favorite fragment
     */
    public static LocalSongFragment newInstance() {
        return new LocalSongFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_song, container, false);
        unbinder = ButterKnife.bind(this, view);
        player = Player.getInstance();
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
        if (isVisibleToUser) {
            init();
        }
    }

    private void init() {
        if (playListAdapter == null) {
            initAdapter();
        } else {
            updateSongs();
        }
    }

    private void updateSongs() {
        List <SongInfo> localSongs = player.getPlayList().getLocalAllSongs();
        playListAdapter.updatePlaySongs(localSongs);
        songListHeader.updateSongCount();
    }

    private void initAdapter() {
        if (rvLocalSong == null) {
            return;
        }
        playListAdapter = new PlayListAdapter(new PlayList(), rvLocalSong) {
            @Override
            public void removeSongAt(int position) {
                SongInfo song = getItem(position);
                LocalSongModel.delete(song);
                super.removeSongAt(position);
                songListHeader.updateSongCount();
            }
        };
        playListAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = playListAdapter.getItem(position);
            playListAdapter.updatePlaySongBackgroundColor(song);
            EventBus.getDefault().post(song);
        });
        playListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (songListHeader != null) {
                songListHeader.showSongEditActivity();
            }
            return false;
        });
        songListHeader = new SongListHeader(getActivity(), playListAdapter, EditType.LOCAL);
    }

    /**
     * Add to local.
     *
     * @param songs the songs
     */
    public void addToLocalSongs(Collection <SongInfo> songs) {
        List <SongInfo> localSongs = player.getPlayList().addToLocalSongs(songs);
        playListAdapter.setNewData(localSongs);
        songListHeader.updateSongCount();
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    public void remove(List <Long> keys) {
        player.getPlayList().getLocalSongs().remove(keys);
        updateSongs();
    }
}
