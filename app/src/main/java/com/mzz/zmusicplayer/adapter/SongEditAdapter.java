package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/4 18:08
 * description :
 */
public class SongEditAdapter extends BaseQuickAdapter <SongInfo, BaseViewHolder> {

    private final RecyclerView recyclerView;
    private TextQueryHandler textQueryHandler;

    public SongEditAdapter(int layoutResId, RecyclerView recyclerView,
                           @Nullable List <SongInfo> data) {
        super(layoutResId, data);
        this.recyclerView = recyclerView;
        Context context = recyclerView.getContext();
        textQueryHandler = new TextQueryHandler(this, context, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(this);
        setHeaderView(getEmptyDummyHeader(context));
    }

    private View getEmptyDummyHeader(Context context) {
        //为使得头部一致，设置一个空头部
        return LayoutInflater.from(context).inflate(R.layout.content_empty, recyclerView, false);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        textQueryHandler.setTextByQueryResult(helper, songInfo, R.id.tv_item_song_name);
        helper.setText(R.id.tv_item_song_artist, songInfo.getArtist());
        helper.setText(R.id.tv_item_song_num,
                String.valueOf(helper.getAdapterPosition()));
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
