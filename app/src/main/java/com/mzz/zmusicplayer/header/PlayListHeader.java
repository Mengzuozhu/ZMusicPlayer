package com.mzz.zmusicplayer.header;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.setting.PlayedMode;

/**
 * @author : Mzz
 * date : 2019 2019/6/7 17:49
 * description :
 */
public class PlayListHeader {

    private TextView tcSongCountAndMode;
    private PlayList playList;
    private FragmentActivity activity;
    private PlayListAdapter playListAdapter;
    private CommonHeader commonHeader;

    public PlayListHeader(FragmentActivity activity, PlayListAdapter playListAdapter) {
        this.activity = activity;
        this.playListAdapter = playListAdapter;
        playList = playListAdapter.getPlayList();
        commonHeader = new CommonHeader(activity, playListAdapter, playList);
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_play_list_header,
                playListAdapter.getRecyclerView(), false);
        tcSongCountAndMode = header.findViewById(R.id.tv_play_song_count_mode);
        updateSongCountAndMode();
        commonHeader.setHeader(header, SongListType.PLAYLIST);
    }

    /**
     * Show song edit activity.
     */
    public void showSongEditActivity() {
        commonHeader.showSongEditActivity(SongListType.PLAYLIST);
    }

    /**
     * Update song count and mode.
     */
    public void updateSongCountAndMode() {
        PlayedMode playMode = playList.getPlayMode();
        String songCountAndMode;
        if (playMode == PlayedMode.SINGLE) {
            songCountAndMode = StringHelper.getLocalFormat("%s", playMode.getDesc());
        } else {
            songCountAndMode = StringHelper.getLocalFormat("%s(%d首)", playMode.getDesc(),
                    playList.getPlaySongs().size());
        }
        tcSongCountAndMode.setText(songCountAndMode);
    }

}
