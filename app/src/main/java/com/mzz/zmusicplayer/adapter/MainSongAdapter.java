package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/1 15:56
 * description :
 */
public class MainSongAdapter extends SongInfoAdapter {
    private int itemSongNameId = R.id.tv_item_song_name;
    private int[] textViewIds = new int[]{itemSongNameId, R.id.tv_item_song_artist,
            R.id.tv_item_song_num};
    private int selectColor;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param songInfos      the song infos
     * @param recyclerView   the recycler view
     * @param context        the context
     * @param isShowCheckBox the is show check box
     */
    public MainSongAdapter(List <SongInfo> songInfos, RecyclerView recyclerView, Context context,
                           boolean isShowCheckBox) {
        super(songInfos, recyclerView, context, isShowCheckBox);
        selectColor = context.getColor(R.color.colorGreen);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        int adapterPosition = helper.getAdapterPosition();
        songInfo.setAdapterPosition(adapterPosition);
        if (songInfo.isPlayListSelected()) {
            changePlaySongColor(helper);
        } else {
            resetPlaySongColor(helper);
        }

        helper.setText(itemSongNameId, songInfo.getName());
    }

    private void resetPlaySongColor(BaseViewHolder helper) {
        setTextViewBackground(helper, Color.WHITE);
        setDivider(helper, Color.TRANSPARENT);
    }

    private void changePlaySongColor(BaseViewHolder helper) {
        setTextViewBackground(helper, selectColor);
        setDivider(helper, selectColor);
    }

    private void setTextViewBackground(BaseViewHolder helper, int color) {
        for (int viewId : textViewIds) {
            TextView textView = helper.getView(viewId);
            if (textView != null) {
                textView.setTextColor(color);
            }
        }
    }

    private void setDivider(BaseViewHolder helper, int color) {
        View divider = helper.getView(R.id.divider_item);
        if (divider != null) {
            divider.setBackgroundColor(color);
        }
    }

}
