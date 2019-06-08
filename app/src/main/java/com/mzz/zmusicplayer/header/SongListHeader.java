package com.mzz.zmusicplayer.header;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * author : Mzz
 * date : 2019 2019/6/7 18:10
 * description :
 */
public class SongListHeader {

    private TextView tvSongCount;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private PlayList mPlayList;
    private PlayListAdapter playListAdapter;

    public SongListHeader(FragmentActivity activity, PlayListAdapter playListAdapter) {
        this.activity = activity;
        this.recyclerView = playListAdapter.getRecyclerView();
        this.playListAdapter = playListAdapter;
        mPlayList = playListAdapter.getMPlayList();
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_song_list_header,
                recyclerView, false);
        tvSongCount = header.findViewById(R.id.tv_song_header_count);
        updateSongCount();
        ImageView ivPlayAll = header.findViewById(R.id.iv_song_header_play_all);
        ivPlayAll.setOnClickListener(v -> onPlayAllClick());
        playListAdapter.setHeaderView(header);
    }

    private void onPlayAllClick() {
        //先开始播放歌曲，再替换播放列表
        SongInfo playingSong = mPlayList.getPlayingSong();
        playListAdapter.updatePlaySongBackgroundColor(playingSong);
        EventBus.getDefault().post(playingSong);
        EventBus.getDefault().post(mPlayList.getPlaySongs());
    }

    /**
     * Update song count.
     */
    public void updateSongCount() {
        String songCountAndMode = StringHelper.getLocalFormat("播放全部(%d首)",
                mPlayList.getPlaySongs().size());
        tvSongCount.setText(songCountAndMode);
    }

}
