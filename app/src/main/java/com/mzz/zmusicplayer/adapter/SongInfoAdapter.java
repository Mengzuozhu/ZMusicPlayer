package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.adapter.CheckableAndDraggableAdapter;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

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
    private RecyclerView recyclerView;
    private boolean isShowCheckBox;
    private int chbSongSelectId;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param songInfos      the song infos
     * @param recyclerView   the recycler view
     * @param context        the context
     * @param isShowCheckBox the is show check box
     */
    SongInfoAdapter(List <SongInfo> songInfos, RecyclerView recyclerView, Context context,
                    boolean isShowCheckBox) {
        super(R.layout.item_song_list, songInfos, recyclerView);
        this.songInfos = songInfos;
        this.recyclerView = recyclerView;
        this.isShowCheckBox = isShowCheckBox;
        chbSongSelectId = R.id.chb_item_song_select;
        if (isShowCheckBox) {
            ViewerHelper.setOnItemClickWithCheckBox(this, chbSongSelectId);
        }
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
        setEmptyDummyHeader(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        helper.setText(R.id.tv_item_song_artist, songInfo.getArtist());
        helper.setText(R.id.tv_item_song_num, String.valueOf(helper.getAdapterPosition()));
        //是否显示复选框
        if (isShowCheckBox) {
            helper.setChecked(chbSongSelectId, songInfo.getIsChecked()).addOnClickListener(chbSongSelectId);
        } else {
            helper.setVisible(chbSongSelectId, false);
        }
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
    public void scrollToPosition(int position) {
        if (getItemCount() > position) {
            recyclerView.scrollToPosition(position);
        }
    }

    @Override
    public int getCheckableViewId() {
        return chbSongSelectId;
    }

    /**
     * Sort by name.
     *
     * @param isAscend the is ascend
     */
    public void sortByName(boolean isAscend) {
        if (isAscend) {
            songInfos.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        } else {
            songInfos.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        }
        notifyDataSetChanged();
    }

    /**
     * Sort by add time.
     */
    public void sortByAddTime() {
        songInfos.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        notifyDataSetChanged();
    }

}
