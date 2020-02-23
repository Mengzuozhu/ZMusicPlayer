package com.mzz.zmusicplayer.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TextQueryHandler;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.edit.RemovedSongInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * @author : Mzz
 * date : 2019 2019/6/3 10:52
 * description :
 */
public class MusicSearchAdapter extends PlayListAdapter {

    private SongListType songListType;
    private TextQueryHandler textQueryHandler;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList     the play list
     * @param recyclerView the recycler view
     */
    public MusicSearchAdapter(PlayList playList, RecyclerView recyclerView) {
        super(playList, recyclerView);
        textQueryHandler = new TextQueryHandler(this, recyclerView.getContext(), itemSongNameId,
                R.id.tv_item_song_artist);
        songListType = playList.getSongListType();
        this.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = getItem(position);
            updatePlaySongBackgroundColor(song);
            EventBus.getDefault().post(song);
        });
    }

    @Override
    public void removeSongAt(int position) {
        SongInfo song = getItem(position);
        EventBus.getDefault().post(new RemovedSongInfo(song, songListType));
        super.removeSongAt(position);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        textQueryHandler.setTextByQueryResult(helper, songInfo);
    }

    /**
     * Sets query text listener.
     *
     * @param searchView the search view
     */
    public void setQueryTextListener(SearchView searchView) {
        textQueryHandler.setQueryTextListener(searchView);
    }

}
