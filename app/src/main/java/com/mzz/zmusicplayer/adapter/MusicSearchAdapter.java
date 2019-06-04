package com.mzz.zmusicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * author : Mzz
 * date : 2019 2019/6/3 10:52
 * description :
 */
public class MusicSearchAdapter extends PlayListAdapter {

    private TextQueryHandler textQueryHandler;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList       the play list
     * @param recyclerView   the recycler view
     * @param isShowCheckBox the is show check box
     */
    public MusicSearchAdapter(PlayList playList, RecyclerView recyclerView,
                              boolean isShowCheckBox) {
        super(playList, recyclerView, isShowCheckBox);
        textQueryHandler = new TextQueryHandler(this, recyclerView.getContext(),
                playList.getSongInfos());
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        textQueryHandler.setTextByQueryResult(helper, songInfo, R.id.tv_item_song_name);
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
