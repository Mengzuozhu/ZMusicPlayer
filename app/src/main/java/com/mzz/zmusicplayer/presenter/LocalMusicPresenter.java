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

import org.greenrobot.eventbus.EventBus;

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
    private PlayListAdapter playListAdapter;
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
        initPlayList(songInfos);
        intiAdapter();
    }

    private void initPlayList(List <SongInfo> songInfos) {
        long lastPlaySongId = AppSetting.getLastPlaySongId();
        int lastPlaySongIndex = PlayList.getSongIndexById(songInfos, lastPlaySongId);
        playList = new PlayList(songInfos, lastPlaySongIndex, AppSetting.getPlayMode());
        localMusicListener.setPlayList(playList);
    }

    private void intiAdapter() {
        playListAdapter = new PlayListAdapter(playList, recyclerView) {
            @Override
            public void removeSongAt(int position) {
                super.removeSongAt(position);
                SongInfo song = getItem(position);
                SongModel.delete(song);
                localMusicListener.setPlayList(playList);
            }
        };
        playListAdapter.setOnItemClickListener((adapter, view, position) -> localMusicListener.setPlayingIndex(position));
        playListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            showSongEditActivity();
            return false;
        });
        initHeader();
        updatePlaySongBackgroundColor(playList.getPlayingSong());
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
        playListAdapter.setHeaderView(header);
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
        if (keys == null || keys.isEmpty()) {
            return;
        }
        SongModel.deleteByKeyInTx(keys);
        List <SongInfo> songInfos = playList.getSongInfos();
        songInfos.removeIf(song -> keys.contains(song.getId()));
        playList.setSongInfos(songInfos);
        localMusicListener.setPlayList(playList);
        playListAdapter.setNewData(songInfos);
        updateSongCountAndMode();
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

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        playListAdapter.updatePlaySongBackgroundColor(song);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return playListAdapter.getLayoutManager();
    }

    @Override
    public void addSongs(List <SongInfo> newSongInfos) {
        playList.addAll(newSongInfos);
        playListAdapter.setNewData(playList.getSongInfos());
        SongModel.insertOrReplaceInTx(newSongInfos);
        updateSongCountAndMode();
    }

    @Override
    public void scrollToFirst() {
        playListAdapter.scrollToPosition(0);
    }

    /**
     * 定位到当前选中播放的歌曲位置
     */
    @Override
    public void locateToSelectedSong() {
        int adapterPosition = playList.getPlayingIndex() + 1;
        playListAdapter.scrollToPosition(adapterPosition);
    }
}
