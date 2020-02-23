package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

import lombok.Getter;

/**
 * 铃声可拖拽适配器
 * @author : Mzz
 * date : 2019 2019/5/11 16:26
 * description :
 */
public class SongInfoAdapter extends BaseQuickAdapter<SongInfo, BaseViewHolder> {

    @Getter
    protected RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<SongInfo> songInfos;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param layoutResId  the layout res id
     * @param songInfos    the song infos
     * @param recyclerView the recycler view
     */
    SongInfoAdapter(int layoutResId, List<SongInfo> songInfos, RecyclerView recyclerView) {
        super(layoutResId, songInfos);
        this.songInfos = songInfos;
        this.recyclerView = recyclerView;
        Context context = recyclerView.getContext();
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
        setEmptyDummyHeader(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        helper.setText(R.id.tv_item_song_artist, songInfo.getArtist());
        helper.setText(R.id.tv_item_song_num, String.valueOf(helper.getAdapterPosition()));
    }

    private void setEmptyDummyHeader(Context context) {
        //为使得头部一致，设置一个空头部
        View header = LayoutInflater.from(context).inflate(R.layout.content_empty,
                recyclerView, false);
        setHeaderView(header);
    }

    /**
     * Scroll to position.
     *
     * @param position the position
     */
    void scrollToPosition(int position) {
        if (position < getItemCount()) {
            recyclerView.scrollToPosition(position);
        }
    }

    /**
     * Scroll to first.
     */
    public void scrollToFirst() {
        scrollToPosition(0);
    }

    /**
     * Sets scroll first show in need.
     *
     * @param fabSongScrollFirst the fab song scroll first
     */
    public void setScrollFirstShowInNeed(FloatingActionButton fabSongScrollFirst) {
        ViewerHelper.setScrollFirstShowInNeed(recyclerView, layoutManager, fabSongScrollFirst);
    }

    /**
     * Sort by name.
     *
     * @param isAscend the is ascend
     */
    public void sortByName(boolean isAscend) {
        LocalSong.sortByChineseName(songInfos, isAscend);
        setNewData(songInfos);
    }

    /**
     * Sort by add time.
     */
    public void sortById() {
        LocalSong.sortById(songInfos);
        setNewData(songInfos);
    }

    /**
     * Sort by play count.
     */
    public void sortByPlayCount() {
        LocalSong.sortByPlayCount(songInfos);
        setNewData(songInfos);
    }

}
