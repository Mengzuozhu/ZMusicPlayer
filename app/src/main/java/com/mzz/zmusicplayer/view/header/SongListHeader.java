package com.mzz.zmusicplayer.view.header;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.PlayListAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * @author : Mzz
 * date : 2019 2019/6/7 18:10
 * description :
 */
public class SongListHeader {

    private TextView tvSongCount;
    private FragmentActivity activity;
    private PlayList playList;
    private SongListType songListType;
    private PlayListAdapter playListAdapter;
    private CommonHeader commonHeader;

    public SongListHeader(FragmentActivity activity, PlayListAdapter playListAdapter,
                          SongListType songListType) {
        this.activity = activity;
        this.playListAdapter = playListAdapter;
        playList = playListAdapter.getPlayList();
        this.songListType = songListType;
        commonHeader = new CommonHeader(activity, playListAdapter, playList);
        initHeader();
    }

    /**
     * Show song edit activity.
     */
    public void showSongEditActivity() {
        commonHeader.showSongEditActivity(songListType);
    }

    /**
     * Update song count.
     */
    public void updateSongCount() {
        int size = playList.getPlaySongs().size();
        String songCountAndMode = StringHelper.getLocalFormat("播放全部(%d首)", size);
        tvSongCount.setText(songCountAndMode);
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_song_list_header,
                playListAdapter.getRecyclerView(), false);
        tvSongCount = header.findViewById(R.id.tv_song_header_count);
        updateSongCount();
        ImageView ivPlayAll = header.findViewById(R.id.iv_song_header_play_all);
        ivPlayAll.setOnClickListener(v -> onPlayAllClick());
        commonHeader.setHeader(header, songListType);
    }

    private void onPlayAllClick() {
        SongInfo playingSong = playList.getPlayingSong();
        if (playingSong == null) {
            return;
        }
        playListAdapter.updatePlaySongBackgroundColor(playingSong);
        //先开始播放歌曲，再替换播放列表
        EventBus.getDefault().post(playingSong);
        EventBus.getDefault().post(playList.getPlaySongs());
    }

}
