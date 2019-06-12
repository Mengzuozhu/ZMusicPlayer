package com.mzz.zmusicplayer.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TextQueryHandler;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/4 18:08
 * description :
 */
public class SongEditAdapter extends SongInfoAdapter {

    private TextQueryHandler textQueryHandler;

    /**
     * Instantiates a new Song edit adapter.
     *
     * @param recyclerView the recycler view
     * @param data         the data
     */
    public SongEditAdapter(RecyclerView recyclerView, @Nullable List <SongInfo> data) {
        super(R.layout.item_song_edit, data, recyclerView);
        textQueryHandler = new TextQueryHandler(this, recyclerView.getContext(),
                R.id.tv_item_song_name, R.id.tv_item_song_artist);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        textQueryHandler.setTextByQueryResult(helper, songInfo);
        helper.addOnClickListener(R.id.iv_edit_del);
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
