package com.mzz.zmusicplayer.presenter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mzz.zmusicplayer.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.contract.PlayListContract;
import com.mzz.zmusicplayer.header.PlayListHeader;
import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.PlayListFragment;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public class PlayListPresenter implements PlayListContract.Presenter, PlayList.PlayListObserver {

    private PlayListHeader playListHeader;
    private RecyclerView recyclerView;
    private PlayListFragment.PlayListListener playListListener;
    private PlayList playList;
    private PlayListAdapter playListAdapter;
    private FragmentActivity activity;

    public PlayListPresenter(PlayListContract.View mView,
                             PlayListFragment.PlayListListener playListListener) {
        activity = mView.getActivity();
        recyclerView = mView.getRecyclerView();
        this.playListListener = playListListener;
        List <SongInfo> orderLocalSongs = LocalSongModel.getOrderLocalSongs();
        playList = new PlayList(orderLocalSongs, AppSetting.getPlayMode());
        playList.setPlayListObserver(this);
        updatePlayList();
        intiAdapter();
    }

    @Override
    public void updatePlayListSongs(List <SongInfo> checkedSongs) {
        playList.updatePlayListSongs(checkedSongs);
        updatePlayList();
        intiAdapter();
    }

    private void updatePlayList() {
        playListListener.setPlayList(playList);
        updateSongCountAndMode();
    }

    private void intiAdapter() {
        playListAdapter = new PlayListAdapter(playList, recyclerView) {
            @Override
            public void removeSongAt(int position) {
                super.removeSongAt(position);
                playList.remove(getItem(position));
                updatePlayList();
            }
        };
        playListHeader = new PlayListHeader(activity, playListAdapter);
        playListAdapter.setOnItemClickListener((adapter, view, position) -> playListListener.setPlayingIndex(position));
        playListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (playListHeader != null) {
                playListHeader.showSongEditActivity();
            }
            return false;
        });
        updatePlaySongBackgroundColor(playList.getPlayingSong());
    }

    private void updateSongCountAndMode() {
        if (playListHeader != null) {
            playListHeader.updateSongCountAndMode();
        }
    }

    @Override
    public void remove(List <Long> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        playList.remove(keys);
        updatePlayList();
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
        playList.addSongs(newSongInfos);
        playListAdapter.setNewData(playList.getPlaySongs());
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

    @Override
    public void onSongCountOrModeChange() {
        updateSongCountAndMode();
    }
}
