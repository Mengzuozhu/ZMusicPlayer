package com.mzz.zmusicplayer.view.presenter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zmusicplayer.manage.AdapterManager;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.PlayListAdapter;
import com.mzz.zmusicplayer.view.contract.PlayListContract;
import com.mzz.zmusicplayer.view.header.PlayListHeader;
import com.mzz.zmusicplayer.view.ui.PlayListFragment;

import java.util.List;

/**
 * @author : Mzz
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
        playList = new PlayList();
        playList.setPlayListObserver(this);
        updatePlayList();
        intiAdapter();
    }

    @Override
    public void updatePlayListSongs(List<SongInfo> checkedSongs) {
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
                playList.remove(getItem(position));
                super.removeSongAt(position);
                updatePlayList();
            }
        };
        AdapterManager.register(playListAdapter);
        playListHeader = new PlayListHeader(activity, playListAdapter);
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
    public void remove(List<Long> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        playList.remove(keys);
        updatePlayList();
    }

    @Override
    public void remove(SongInfo song) {
        int songIndexById = PlayList.getSongIndexById(playList.getPlaySongs(), song.getId());
        playListAdapter.removeSongAt(songIndexById);
        playList.remove(song);
        updatePlayList();
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        playListAdapter.updatePlaySongBackgroundColor(song);
    }

    @Override
    public void setScrollFirstShowInNeed(FloatingActionButton floatingActionButton) {
        playListAdapter.setScrollFirstShowInNeed(floatingActionButton);

    }

    @Override
    public void scrollToFirst() {
        playListAdapter.scrollToFirst();
    }

    /**
     * 定位到当前选中播放的歌曲位置
     */
    @Override
    public void locateToSelectedSong() {
        playListAdapter.locateToSelectedSong();
    }

    @Override
    public void onSongCountOrModeChange() {
        updateSongCountAndMode();
    }
}
