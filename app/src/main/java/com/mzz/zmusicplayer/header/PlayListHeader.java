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
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.setting.SongOrderMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.MusicSearchActivity;
import com.mzz.zmusicplayer.ui.SongEditActivity;

import java.util.ArrayList;

/**
 * author : Mzz
 * date : 2019 2019/6/7 17:49
 * description :
 */
public class PlayListHeader {

    private TextView tcSongCountAndMode;
    private PlayList playList;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private PlayListAdapter playListAdapter;

    public PlayListHeader(FragmentActivity activity, PlayListAdapter playListAdapter) {
        this.activity = activity;
        this.recyclerView = playListAdapter.getRecyclerView();
        this.playListAdapter = playListAdapter;
        playList = playListAdapter.getMPlayList();
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_play_list_header,
                recyclerView, false);
        tcSongCountAndMode = header.findViewById(R.id.tv_play_song_count_mode);
        updateSongCountAndMode();
        ImageView searchView = header.findViewById(R.id.iv_play_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_play_header_sort);
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        ImageView editView = header.findViewById(R.id.iv_play_header_edit);
        editView.setOnClickListener(v -> showSongEditActivity());
        playListAdapter.setHeaderView(header);
    }

    /**
     * Show song edit activity.
     */
    public void showSongEditActivity() {
        SongEditActivity.startForResult(activity, (ArrayList <SongInfo>) playList.getPlaySongs(),
                EditType.PLAYLIST);
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
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_ASCEND_BY_NAME);
                    return true;
                case R.id.action_sort_descend_by_name:
                    playListAdapter.sortByName(false);
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_DESCEND_BY_NAME);
                    return true;
                case R.id.action_sort_by_add_time:
                    playListAdapter.sortById();
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_ASCEND_BY_ADD_TIME);
                    return true;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * Update song count and mode.
     */
    public void updateSongCountAndMode() {
        PlayedMode playMode = playList.getPlayMode();
        String songCountAndMode;
        if (playMode == PlayedMode.SINGLE) {
            songCountAndMode = StringHelper.getLocalFormat("%s", playMode.getDesc(),
                    playList.getPlaySongs().size());
        } else {
            songCountAndMode = StringHelper.getLocalFormat("%s(%d首)", playMode.getDesc(),
                    playList.getPlaySongs().size());
        }
        tcSongCountAndMode.setText(songCountAndMode);
    }

}
