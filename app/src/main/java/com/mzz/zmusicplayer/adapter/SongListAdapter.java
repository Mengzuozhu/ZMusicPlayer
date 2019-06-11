package com.mzz.zmusicplayer.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.mzz.zmusicplayer.header.SongListHeader;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/9 16:11
 * description :
 */
public class SongListAdapter extends PlayListAdapter {

    private SongListHeader songListHeader;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList     the play list
     * @param recyclerView the recycler view
     */
    protected SongListAdapter(PlayList playList, RecyclerView recyclerView,
                              FragmentActivity activity) {
        super(playList, recyclerView);
        setOnItemLongClick();
        songListHeader = new SongListHeader(activity, this, playList.getSongListType());
    }

    protected void updateSongCount() {
        songListHeader.updateSongCount();
    }

    /**
     * Update data.
     *
     * @param songs the songs
     */
    public void updateData(List <SongInfo> songs) {
        updatePlaySongs(songs);
        updateSongCount();
    }

    private void setOnItemLongClick() {
        setOnItemLongClickListener((adapter, view, position) -> {
            if (songListHeader != null) {
                songListHeader.showSongEditActivity();
            }
            return false;
        });
    }

}
