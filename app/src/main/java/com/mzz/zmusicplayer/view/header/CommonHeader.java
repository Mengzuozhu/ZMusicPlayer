package com.mzz.zmusicplayer.view.header;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.config.AppSetting;
import com.mzz.zmusicplayer.enums.SongOrderMode;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.enums.SongListType;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.view.ui.MusicSearchActivity;
import com.mzz.zmusicplayer.view.ui.SongEditActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : Mzz
 * date : 2019 2019/6/13 19:55
 * description :
 */
class CommonHeader {

    private FragmentActivity activity;
    private PlayListAdapter playListAdapter;
    private PlayList playList;

    CommonHeader(FragmentActivity activity, PlayListAdapter playListAdapter,
                 PlayList playList) {
        this.activity = activity;
        this.playListAdapter = playListAdapter;
        this.playList = playList;
    }

    /**
     * Sets header.
     *
     * @param header       the header
     * @param songListType the song list type
     */
    void setHeader(View header, SongListType songListType) {
        ImageView searchView = header.findViewById(R.id.iv_song_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_song_header_sort);
        //最近列表不支持排序
        if (songListType == SongListType.RECENT) {
            sortView.setVisibility(View.GONE);
        }
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        ImageView editView = header.findViewById(R.id.iv_song_header_edit);
        editView.setOnClickListener(v -> showSongEditActivity(songListType));
        playListAdapter.setHeaderView(header);
    }

    /**
     * Show song edit activity.
     */
    void showSongEditActivity(SongListType songListType) {
        List<SongInfo> songs = playList.getPlaySongs();
        if (songs instanceof LinkedList) {
            songs = new ArrayList<>(songs);
        }
        SongEditActivity.startForResult(activity, songs, songListType);
    }

    /**
     * Show search activity.
     */
    private void showSearchActivity() {
        MusicSearchActivity.startForResult(activity, playList);
    }

    /**
     * Show song order popup menu.
     *
     * @param view the view
     */
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
                case R.id.action_sort_by_play_count:
                    playListAdapter.sortByPlayCount();
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_DESCEND_BY_PLAY_COUNT);
                    return true;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

}
