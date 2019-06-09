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
import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.song.LocalSongClass;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class RecentFragment extends Fragment {

    @BindView(R.id.rv_recent_song)
    RecyclerView rvRecentSong;
    private Unbinder unbinder;
    private LocalSongClass localSongs;
    private SongListAdapter songListAdapter;

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
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        unbinder = ButterKnife.bind(this, view);
        localSongs = LocalSongClass.getInstance();
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
        if (isVisibleToUser) {
            init();
        }
    }

    private void init() {
        if (songListAdapter == null) {
            initAdapter();
        } else {
            List <SongInfo> recentSongs = localSongs.getRecentSongs();
            songListAdapter.updateData(recentSongs);
        }
    }

    private void initAdapter() {
        if (rvRecentSong == null) {
            return;
        }
        songListAdapter = new SongListAdapter(new PlayList(), rvRecentSong, getActivity(),
                EditType.RECENT) {
            @Override
            public void removeSongAt(int position) {
                SongInfo song = this.getItem(position);
                if (song == null) {
                    return;
                }
                song.setLastPlayTime(null);
                LocalSongModel.update(song);
                super.removeSongAt(position);
                updateSongCount();
            }
        };
    }

    public void remove(List <Long> keys) {
//        player.getPlayList().getLocalSongs().remove(keys);
//        updateSongs();
    }

}
