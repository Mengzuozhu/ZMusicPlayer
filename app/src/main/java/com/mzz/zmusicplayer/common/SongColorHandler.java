package com.mzz.zmusicplayer.common;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * @author : Mzz
 * date : 2019 2019/6/12 10:27
 * description :
 */
public class SongColorHandler {
    private static final int[] TEXT_VIEW_IDS = new int[]{R.id.tv_item_song_name,
            R.id.tv_item_song_artist, R.id.tv_item_song_num};
    private PlayList playList;
    private int selectColor;

    public SongColorHandler(PlayList playList, int selectColor) {
        this.playList = playList;
        this.selectColor = selectColor;
    }

    /**
     * Sets song color.
     *
     * @param helper   the helper
     * @param songInfo the song info
     */
    public void setSongColor(BaseViewHolder helper, SongInfo songInfo) {
        if (songInfo.isPlayListSelected()) {
            changePlaySongColor(helper);
            int adapterPosition = helper.getAdapterPosition();
            //排序后，播放位置可能变化，因此重新设置播放位置
            playList.setPlayingIndex(adapterPosition - 1);
        } else {
            resetPlaySongColor(helper);
        }
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
        for (int viewId : TEXT_VIEW_IDS) {
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
