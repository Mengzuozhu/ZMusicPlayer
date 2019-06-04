package com.mzz.zmusicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * 支持歌曲查询的适配器
 * author : Mzz
 * date : 2019 2019/6/1 16:05
 * description :
 */
public class SongQueryAdapter extends SongInfoAdapter {

    private TextQueryHandler textQueryHandler;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param songInfos      the song infos
     * @param recyclerView   the recycler view
     * @param isShowCheckBox the is show check box
     */
    public SongQueryAdapter(List <SongInfo> songInfos, RecyclerView recyclerView,
                            boolean isShowCheckBox) {
        super(songInfos, recyclerView, isShowCheckBox);
        textQueryHandler = new TextQueryHandler(this, recyclerView.getContext(), songInfos);
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
