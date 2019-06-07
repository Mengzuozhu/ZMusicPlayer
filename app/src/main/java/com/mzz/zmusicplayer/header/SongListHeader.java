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
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayListType;
import com.mzz.zmusicplayer.song.PlayList;

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
    private PlayListType playListType;
    private PlayListAdapter playListAdapter;

    public SongListHeader(FragmentActivity activity, PlayListAdapter playListAdapter,
                             PlayListType playListType) {
        this.activity = activity;
        this.recyclerView = playListAdapter.getRecyclerView();
        this.playListAdapter = playListAdapter;
        mPlayList = playListAdapter.getPlayList();
        this.playListType = playListType;
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_song_list_header,
                recyclerView, false);
        tvSongCount = header.findViewById(R.id.tv_header_song_count);
        updateSongCount();
        ImageView ivPlayAll = header.findViewById(R.id.iv_header_play_all);
        ivPlayAll.setOnClickListener(v -> onPlayAllClick());
        playListAdapter.setHeaderView(header);
    }

    public void onPlayAllClick() {
        EventBus.getDefault().post(mPlayList.getPlayingSong());
        EventBus.getDefault().post(mPlayList.getSongs());
        AppSetting.setPlayListType(playListType);
    }

    public void updateSongCount() {
        String songCountAndMode = StringHelper.getLocalFormat("播放全部(%d首)",
                mPlayList.getSongs().size());
        tvSongCount.setText(songCountAndMode);
    }

}
