package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongListAdapter;
import com.mzz.zmusicplayer.edit.EditType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.ISongChangeListener;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;

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
public class LocalSongFragment extends Fragment implements ISongChangeListener {

    @BindView(R.id.rv_local_song)
    RecyclerView rvLocalSong;
    Unbinder unbinder;
    private SongListAdapter songListAdapter;
    private LocalSong localSongs;
    private boolean isVisibleToUser;

    /**
     * New instance favorite fragment.
     *
     * @return the favorite fragment
     */
    public static LocalSongFragment newInstance() {
        return new LocalSongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_song, container, false);
        unbinder = ButterKnife.bind(this, view);
        localSongs = LocalSong.getInstance();
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
            List <SongInfo> allLocalSongs = localSongs.getAllLocalSongs();
            songListAdapter.updateData(allLocalSongs);
        }
    }

    private void initAdapter() {
        if (rvLocalSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(), rvLocalSong, getActivity(),
                EditType.LOCAL) {
            @Override
            public void removeSongAt(int position) {
                localSongs.remove(getItem(position));
                super.removeSongAt(position);
                updateSongCount();
            }
        };
    }

    /**
     * Add to local.
     *
     * @param songs the songs
     */
    public void addToLocalSongs(Collection <SongInfo> songs) {
        songListAdapter.updateData(localSongs.addToLocalSongs(songs));
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    public void remove(List <Long> keys) {
        songListAdapter.updateData(localSongs.remove(keys));
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        if (!isVisibleToUser) {
            return;
        }
        songListAdapter.updatePlaySongBackgroundColor(song);
    }
}
