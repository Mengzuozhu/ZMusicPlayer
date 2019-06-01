package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TextQueryHandler;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持歌曲查询的适配器
 * author : Mzz
 * date : 2019 2019/6/1 16:05
 * description :
 */
public class SongQueryAdapter extends SongInfoAdapter {

    private Map <String, Spannable> nameAndQuerySpans = new HashMap <>();
    private List <SongInfo> songInfos;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param songInfos      the song infos
     * @param recyclerView   the recycler view
     * @param context        the context
     * @param isShowCheckBox the is show check box
     */
    public SongQueryAdapter(List <SongInfo> songInfos, RecyclerView recyclerView, Context context
            , boolean isShowCheckBox) {
        super(songInfos, recyclerView, context, isShowCheckBox);
        this.songInfos = songInfos;
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        String name = songInfo.getName();
        int itemSongNameId = R.id.tv_item_song_name;
        if (nameAndQuerySpans.containsKey(name)) {
            helper.setText(itemSongNameId, nameAndQuerySpans.get(name));
        } else {
            helper.setText(itemSongNameId, name);
        }
    }

    /**
     * Sets query text listener.
     *
     * @param searchView the search view
     * @param color      the color
     */
    public void setQueryTextListener(SearchView searchView, @ColorInt int color) {
        new TextQueryHandler(searchView, this, songInfos, nameAndQuerySpans).setQueryTextListener(color);
    }

}
