package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.adapter.CheckableAndDraggableAdapter;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TextQueryHandler;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * 铃声可拖拽适配器
 * author : Mzz
 * date : 2019 2019/5/11 16:26
 * description :
 */
public class SongInfoAdapter extends CheckableAndDraggableAdapter <SongInfo> {

    @Getter
    private LinearLayoutManager layoutManager;
    private List <SongInfo> songInfos;
    private int chbSongSelectId;
    private Map <String, Spannable> nameAndQuerySpans = new HashMap <>();

    public SongInfoAdapter(List <SongInfo> songInfos, RecyclerView recyclerView, Context context) {
        super(R.layout.item_song, songInfos, recyclerView);
        this.songInfos = songInfos;
        chbSongSelectId = R.id.chb_song_select;
        ViewerHelper.setOnItemClickWithCheckBox(this, R.id.chb_song_select);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo item) {
        String name = item.getName();
        if (nameAndQuerySpans.containsKey(name)) {
            helper.setText(R.id.tv_song_name, nameAndQuerySpans.get(name));
        } else {
            helper.setText(R.id.tv_song_name, name);
        }
        helper.setChecked(chbSongSelectId, item.getIsChecked()).addOnClickListener(chbSongSelectId);
    }

    public void setQueryTextListener(SearchView svAlarmSong) {
        new TextQueryHandler(svAlarmSong, this, songInfos, nameAndQuerySpans).setQueryTextListener();
    }

    @Override
    public int getCheckableViewId() {
        return chbSongSelectId;
    }

    public void sort(boolean isAscend) {
        if (isAscend) {
            getData().sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        } else {
            getData().sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        }
        notifyDataSetChanged();
    }
}
