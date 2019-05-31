package com.mzz.zmusicplayer.common;

import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mzz.zandroidcommon.view.TextQueryHelper;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;
import java.util.Map;

/**
 * author : Mzz
 * date : 2019 2019/5/8 9:15
 * description :
 */
public class TextQueryHandler {

    private SearchView svAlarmSong;
    private BaseQuickAdapter adapter;
    private List <SongInfo> songInfos;
    private Map <String, Spannable> nameAndQuerySpans;

    public TextQueryHandler(SearchView svAlarmSong, BaseQuickAdapter adapter,
                            List <SongInfo> songInfos, Map <String, Spannable> nameAndQuerySpans) {
        this.svAlarmSong = svAlarmSong;
        this.adapter = adapter;
        this.songInfos = songInfos;
        this.nameAndQuerySpans = nameAndQuerySpans;
    }

    /**
     * Sets query text listener.
     */
    public void setQueryTextListener(@ColorInt int color) {
        svAlarmSong.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List <SongInfo> queryResult = TextQueryHelper.getQueryResult(songInfos, query,
                        nameAndQuerySpans, color);
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
