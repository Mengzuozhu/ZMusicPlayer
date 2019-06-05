package com.mzz.zmusicplayer.presenter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.contract.LocalMusicContract;
import com.mzz.zmusicplayer.model.SongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.setting.SongOrderMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.LocalMusicFragment;
import com.mzz.zmusicplayer.ui.MusicSearchActivity;
import com.mzz.zmusicplayer.ui.SongEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public class LocalMusicPresenter implements LocalMusicContract.Presenter {

    private TextView tcSongCountAndMode;
    private RecyclerView recyclerView;
    private LocalMusicFragment.LocalMusicListener localMusicListener;
    private PlayList playList;
    private PlayListAdapter baseAdapter;
    private FragmentActivity activity;

    public LocalMusicPresenter(LocalMusicContract.View mView,
                               LocalMusicFragment.LocalMusicListener localMusicListener) {
        activity = mView.getActivity();
        recyclerView = mView.getRecyclerView();
        this.localMusicListener = localMusicListener;
        init();
    }

    private void init() {
        List <SongInfo> songInfos = SongModel.getOrderSongInfos();
        long lastPlaySongId = AppSetting.getLastPlaySongId();
        int lastPlaySongIndex = PlayList.getSongIndexById(songInfos, lastPlaySongId);
        playList = new PlayList(songInfos, lastPlaySongIndex, AppSetting.getPlayMode());
        intiAdapter();
        updatePlaySongBackgroundColor(playList.getPlayingSong());
        localMusicListener.setPlayList(playList);
    }

    private void intiAdapter() {
        baseAdapter = new PlayListAdapter(playList, recyclerView);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> localMusicListener.setPlayingIndex(position));
        baseAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            showSongEditActivity();
            return false;
        });
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(activity).inflate(R.layout.content_song_header,
                recyclerView, false);
        tcSongCountAndMode = header.findViewById(R.id.tv_song_count_mode);
        updateSongCountAndMode();
        ImageView searchView = header.findViewById(R.id.iv_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_header_sort);
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        ImageView editView = header.findViewById(R.id.iv_header_edit);
        editView.setOnClickListener(v -> showSongEditActivity());
        baseAdapter.setHeaderView(header);
    }

    @Override
    public void updateSongCountAndMode() {
        PlayedMode playMode = playList.getPlayMode();
        String songCountAndMode;
        if (playMode == PlayedMode.SINGLE) {
            songCountAndMode = StringHelper.getLocalFormat("%s", playMode.getDesc(),
                    playList.getSongInfos().size());
        } else {
            songCountAndMode = StringHelper.getLocalFormat("%s(%d首)", playMode.getDesc(),
                    playList.getSongInfos().size());
        }
        tcSongCountAndMode.setText(songCountAndMode);
    }

    @Override
    public void deleteByKeyInTx(List <Long> keys) {
        if (keys.isEmpty()) {
            return;
        }
        SongModel.deleteByKeyInTx(keys);
        init();
    }

    @Override
    public void finishMainActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    private void showSongEditActivity() {
        SongEditActivity.startForResult(activity, (ArrayList <SongInfo>) playList.getSongInfos());
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
                    baseAdapter.sortByName(true);
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_ASCEND_BY_NAME);
                    return true;
                case R.id.action_sort_descend_by_name:
                    baseAdapter.sortByName(false);
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_DESCEND_BY_NAME);
                    return true;
                case R.id.action_sort_by_add_time:
                    baseAdapter.sortById();
                    AppSetting.setSongOrderMode(SongOrderMode.ORDER_ASCEND_BY_ADD_TIME);
                    return true;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        baseAdapter.updatePlaySongBackgroundColor(song);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return baseAdapter.getLayoutManager();
    }

    @Override
    public void addSongs(List <SongInfo> newSongInfos) {
        playList.addAll(newSongInfos);
        baseAdapter.setNewData(playList.getSongInfos());
        SongModel.insertOrReplaceInTx(newSongInfos);
        updateSongCountAndMode();
    }

    @Override
    public void scrollToFirst() {
        baseAdapter.scrollToPosition(0);
    }

    /**
     * 定位到当前播放的歌曲位置
     */
    @Override
    public void locateToSelectedSong() {
        SongInfo playingSong = playList.getPlayingSong();
        if (playingSong != null) {
            baseAdapter.scrollToPosition(playingSong.getAdapterPosition());
        }
    }
}