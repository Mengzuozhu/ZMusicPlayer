package com.mzz.zmusicplayer.common;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

/**
 * @author : Mzz
 * date : 2019 2019/5/8 9:15
 * description :
 */
public class TextQueryHandler {

    @Setter
    @IdRes
    private int queryColor;
    private BaseQuickAdapter<SongInfo, BaseViewHolder> adapter;
    private List<SongInfo> songInfos;
    private int itemSongNameId;
    private int itemSongArtistId;
    private Map<String, Spannable> nameAndQuerySpans;

    public TextQueryHandler(BaseQuickAdapter<SongInfo, BaseViewHolder> adapter, Context context,
                            @IdRes int itemSongNameId, @IdRes int itemSongArtistId) {
        this.adapter = adapter;
        this.songInfos = adapter.getData();
        this.itemSongNameId = itemSongNameId;
        this.itemSongArtistId = itemSongArtistId;
        this.nameAndQuerySpans = new HashMap<>();
        queryColor = context.getColor(R.color.colorRed);
    }

    /**
     * Sets text by query result.
     *
     * @param helper   the helper
     * @param songInfo the song info
     */
    public void setTextByQueryResult(BaseViewHolder helper, SongInfo songInfo) {
        String name = songInfo.getName();
        setSpan(helper, name, itemSongNameId);
        String artist = songInfo.getArtist();
        setSpan(helper, artist, itemSongArtistId);
    }

    /**
     * Sets query text listener.
     */
    public void setQueryTextListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<SongInfo> queryResult = querySongNameAndArtist(query);
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

    private void setSpan(BaseViewHolder helper, String value, int viewId) {
        if (nameAndQuerySpans.containsKey(value)) {
            helper.setText(viewId, nameAndQuerySpans.get(value));
        } else {
            helper.setText(viewId, value);
        }
    }

    private List<SongInfo> querySongNameAndArtist(String queryText) {
        List<SongInfo> queryResults = new ArrayList<>();
        if (nameAndQuerySpans != null) {
            nameAndQuerySpans.clear();
        } else {
            nameAndQuerySpans = new HashMap<>();
        }
        if (songInfos == null || StringUtils.isEmpty(queryText)) {
            return queryResults;
        }

        int queryLength = queryText.length();
        String lowerText = queryText.toLowerCase();
        for (SongInfo song : songInfos) {
            String name = song.getName();
            String artist = song.getArtist();
            int nameIndex = name.toLowerCase().indexOf(lowerText);
            int artistIndex = artist.toLowerCase().indexOf(lowerText);
            addSpan(queryLength, name, nameIndex);
            addSpan(queryLength, artist, artistIndex);
            if (nameIndex >= 0 || artistIndex >= 0) {
                queryResults.add(song);
            }
        }
        return queryResults;

    }

    private void addSpan(int queryLength, String name, int nameIndex) {
        if (nameIndex < 0) {
            return;
        }
        Spannable span = new SpannableString(name);
        span.setSpan(new ForegroundColorSpan(queryColor), nameIndex,
                nameIndex + queryLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        nameAndQuerySpans.put(name, span);
    }

}
