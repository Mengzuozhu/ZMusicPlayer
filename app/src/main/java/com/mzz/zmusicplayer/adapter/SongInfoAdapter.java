package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.adapter.CheckableAndDraggableAdapter;
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
        super(R.layout.item_song_list, songInfos, recyclerView);
        this.songInfos = songInfos;
        chbSongSelectId = R.id.chb_item_song_select;
//        ViewerHelper.setOnItemClickWithCheckBox(this, chbSongSelectId);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        String name = songInfo.getName();
        int itemSongNameId = R.id.tv_item_song_name;
        if (nameAndQuerySpans.containsKey(name)) {
            helper.setText(itemSongNameId, nameAndQuerySpans.get(name));
        } else {
            helper.setText(itemSongNameId, name);
        }
        int itemSongArtistId = R.id.tv_item_song_artist;
        helper.setText(itemSongArtistId, songInfo.getArtist());
        helper.setChecked(chbSongSelectId, songInfo.getIsChecked()).addOnClickListener(chbSongSelectId);
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
