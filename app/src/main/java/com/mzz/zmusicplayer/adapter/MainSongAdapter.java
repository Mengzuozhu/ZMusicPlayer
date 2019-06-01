package com.mzz.zmusicplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

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
    private PlayList playList;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList       the play list
     * @param recyclerView   the recycler view
     * @param context        the context
     * @param isShowCheckBox the is show check box
     */
    public MainSongAdapter(PlayList playList, RecyclerView recyclerView, Context context,
                           boolean isShowCheckBox) {
        super(playList.getSongInfos(), recyclerView, context, isShowCheckBox);
        this.playList = playList;
        selectColor = context.getColor(R.color.colorGreen);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        int adapterPosition = helper.getAdapterPosition();
        songInfo.setAdapterPosition(adapterPosition);
        if (songInfo.isPlayListSelected()) {
            changePlaySongColor(helper);
            //排序后，播放位置可能变化，因此重新设置播放位置
            playList.setPlayingIndex(adapterPosition - 1);
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
