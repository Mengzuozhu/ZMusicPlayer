package com.mzz.zmusicplayer.view.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zmusicplayer.manage.AdapterManager;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.header.SongListHeader;

import java.util.List;

/**
 * @author : Mzz
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
        AdapterManager.register(this);
    }

    private void updateSongCount() {
        songListHeader.updateSongCount();
    }

    @Override
    public void removeSongAt(int position) {
        super.removeSongAt(position);
        updateSongCount();
    }

    public void removeSong(SongInfo song) {
        int songIndexById = PlayList.getSongIndexById(getData(), song.getId());
        removeSongAt(songIndexById);
    }

    /**
     * Update data.
     *
     * @param songs the songs
     */
    public void updateData(List<SongInfo> songs) {
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
