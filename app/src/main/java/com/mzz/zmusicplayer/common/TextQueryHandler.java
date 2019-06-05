package com.mzz.zmusicplayer.common;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.view.TextQueryHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

/**
 * author : Mzz
 * date : 2019 2019/5/8 9:15
 * description :
 */
public class TextQueryHandler {

    @Setter
    @IdRes
    private int queryColor;
    private BaseQuickAdapter adapter;
    private List <SongInfo> songInfos;
    private Map <String, Spannable> nameAndQuerySpans;

    public TextQueryHandler(BaseQuickAdapter adapter, Context context, List <SongInfo> songInfos) {
        this.adapter = adapter;
        this.songInfos = songInfos;
        this.nameAndQuerySpans = new HashMap <>();
        queryColor = context.getColor(R.color.colorRed);
    }

    /**
     * Sets text by query result.
     *
     * @param helper         the helper
     * @param songInfo       the song info
     * @param itemSongNameId the item song name id
     */
    public void setTextByQueryResult(BaseViewHolder helper, SongInfo songInfo,
                                     @IdRes int itemSongNameId) {
        String name = songInfo.getName();
        if (nameAndQuerySpans.containsKey(name)) {
            helper.setText(itemSongNameId, nameAndQuerySpans.get(name));
        } else {
            helper.setText(itemSongNameId, name);
        }
    }

    /**
     * Sets query text listener.
     */
    public void setQueryTextListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List <SongInfo> queryResult = TextQueryHelper.getQueryResult(songInfos, query,
                        nameAndQuerySpans, queryColor);
                adapter.setNewData(queryResult);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    nameAndQuerySpans.clear();
                    adapter.setNewData(songInfos);
                }
                return false;
            }
        });
    }

}
