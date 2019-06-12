package com.mzz.zmusicplayer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/1 15:56
 * description :
 */
public class PlayListAdapter extends SongInfoAdapter {
    int itemSongNameId = R.id.tv_item_song_name;
    private int[] textViewIds = new int[]{itemSongNameId, R.id.tv_item_song_artist,
            R.id.tv_item_song_num};
    private int selectColor;
    @Getter
    private PlayList playList;
    private SongInfo currentColorSong;
    private Context context;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList     the play list
     * @param recyclerView the recycler view
     */
    protected PlayListAdapter(PlayList playList, RecyclerView recyclerView) {
        super(R.layout.item_song_list, playList.getPlaySongs(), recyclerView);
        this.playList = playList;
        context = recyclerView.getContext();
        selectColor = context.getColor(R.color.colorGreen);
        setOnItemChildClickListener((adapter, view, position) -> showSongMoreMenu(view, position));
        setPlaySongClickListener();
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        setSongColor(helper, songInfo);
        helper.addOnClickListener(R.id.iv_song_more);
        helper.setText(itemSongNameId, songInfo.getName());
    }

    private void setSongColor(BaseViewHolder helper, SongInfo songInfo) {
        if (songInfo.isPlayListSelected()) {
            changePlaySongColor(helper);
            int adapterPosition = helper.getAdapterPosition();
            //排序后，播放位置可能变化，因此重新设置播放位置
            playList.setPlayingIndex(adapterPosition - 1);
        } else {
            resetPlaySongColor(helper);
        }
    }

    /**
     * Update play song background color.
     *
     * @param song the song
     */
    public void updatePlaySongBackgroundColor(SongInfo song) {
        //重置上一次选中的歌曲
        if (currentColorSong != null) {
            currentColorSong.setPlayListSelected(false);
        }
        if (song != null) {
            song.setPlayListSelected(true);
            currentColorSong = song;
            notifyDataSetChanged();
        }
    }

    /**
     * Remove song at.
     *
     * @param position the position
     */
    public void removeSongAt(int position) {
        remove(position);
        notifyDataSetChanged();
    }

    /**
     * Locate to selected song.
     */
    public void locateToSelectedSong() {
        int adapterPosition = playList.getPlayingIndex() + 1;
        scrollToPosition(adapterPosition);
    }

    void updatePlaySongs(List <SongInfo> songs) {
        playList.setPlaySongs(songs);
        setNewData(songs);
    }

    private void setPlaySongClickListener() {
        this.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = getItem(position);
            EventBus.getDefault().post(song);
        });
    }

    private void showSongMoreMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_song_more);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_song_delete) {
                removeSongAt(position);
                return true;
            } else if (itemId == R.id.action_song_detail) {
                SongInfo songInfo = PlayListAdapter.this.getItem(position);
                if (songInfo == null) {
                    return true;
                }
                showSongDetail(songInfo);
            }
            return false;
        });
        popupMenu.show();
    }

    private void showSongDetail(SongInfo songInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("歌曲信息")
                .setMessage(songInfo.getSongDetail())
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss());
        builder.create().show();
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
