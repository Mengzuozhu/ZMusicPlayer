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
import com.mzz.zmusicplayer.edit.EditType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.MusicSearchActivity;
import com.mzz.zmusicplayer.ui.SongEditActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/7 18:10
 * description :
 */
public class SongListHeader {

    private TextView tvSongCount;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private PlayList playList;
    private EditType editType;
    private PlayListAdapter playListAdapter;

    public SongListHeader(FragmentActivity activity, PlayListAdapter playListAdapter,
                          EditType editType) {
        this.activity = activity;
        this.recyclerView = playListAdapter.getRecyclerView();
        this.playListAdapter = playListAdapter;
        playList = playListAdapter.getPlayList();
        this.editType = editType;
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_song_list_header,
                recyclerView, false);
        tvSongCount = header.findViewById(R.id.tv_song_header_count);
        updateSongCount();
        ImageView searchView = header.findViewById(R.id.iv_song_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView ivPlayAll = header.findViewById(R.id.iv_song_header_play_all);
        ivPlayAll.setOnClickListener(v -> onPlayAllClick());
        ImageView editView = header.findViewById(R.id.iv_song_header_edit);
        editView.setOnClickListener(v -> showSongEditActivity());
        playListAdapter.setHeaderView(header);
    }

    /**
     * Show song edit activity.
     */
    public void showSongEditActivity() {
        List <SongInfo> songs = playList.getPlaySongs();
        if (songs instanceof LinkedList) {
            songs = new ArrayList <>(songs);
        }
        SongEditActivity.startForResult(activity, songs, editType);
    }

    private void showSearchActivity() {
        MusicSearchActivity.startForResult(activity, playList);
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

    /**
     * Update song count.
     */
    public void updateSongCount() {
        int size = playList.getPlaySongs().size();
        String songCountAndMode = StringHelper.getLocalFormat("播放全部(%d首)", size);
        tvSongCount.setText(songCountAndMode);
    }

}
