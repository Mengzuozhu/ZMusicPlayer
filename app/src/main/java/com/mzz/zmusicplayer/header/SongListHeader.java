package com.mzz.zmusicplayer.header;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
        ImageView ivPlayAll = header.findViewById(R.id.iv_song_header_play_all);
        ivPlayAll.setOnClickListener(v -> onPlayAllClick());
        ImageView searchView = header.findViewById(R.id.iv_song_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_song_header_sort);
        if (editType == EditType.RECENT) {
            sortView.setVisibility(View.GONE);
        }
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
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

    private void showSongOrderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.menu_song_sort_by_time);
        popupMenu.inflate(R.menu.menu_sort_by_name);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            switch (itemId) {
                case R.id.action_sort_ascend_by_name:
                    playListAdapter.sortByName(true);
                    return true;
                case R.id.action_sort_descend_by_name:
                    playListAdapter.sortByName(false);
                    return true;
                case R.id.action_sort_by_add_time:
                    playListAdapter.sortById();
                    return true;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
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
