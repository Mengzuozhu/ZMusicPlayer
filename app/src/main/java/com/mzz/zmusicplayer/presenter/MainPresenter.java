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
import com.mzz.zmusicplayer.adapter.MainSongAdapter;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.model.SongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.setting.SongOrderMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.SearchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public class MainPresenter implements MainContract.Presenter {

    private TextView tcSongCountAndMode;
    private RecyclerView recyclerView;
    private PlayList playList;
    private MainSongAdapter baseAdapter;
    private MainContract.View mView;
    private FragmentActivity context;
    private SongInfo currentColorSong;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        context = mView.getActivity();
        recyclerView = mView.getRecyclerView();
        initSongInfos();
        intiAdapter();
        mView.updatePlayList(playList);
        //设置缓存大小，避免多个item出现选中颜色
        recyclerView.setItemViewCacheSize(playList.getSongInfos().size() + 1);
    }

    private void initSongInfos() {
        List <SongInfo> songInfos = SongModel.getOrderSongInfos();
        playList = new PlayList(songInfos, AppSetting.getLastPlaySongIndex(),
                AppSetting.getPlayMode());
    }

    private void intiAdapter() {
        baseAdapter = new MainSongAdapter(playList, recyclerView, context, false);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            playList.setPlayingIndex(position);
            //歌单的顺序可能变化了，所以更新歌曲列表
            mView.updatePlayList(playList);
        });
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(context).inflate(R.layout.content_song_header,
                recyclerView, false);
        tcSongCountAndMode = header.findViewById(R.id.tv_song_count_mode);
        updateSongCountAndMode();
        ImageView searchView = header.findViewById(R.id.iv_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_header_sort);
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        baseAdapter.setHeaderView(header);
        //隐藏头部
//        scrollToPosition(1);
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

    private void showSearchActivity() {
        SearchActivity.startForResult(context, (ArrayList <SongInfo>) playList.getSongInfos());
    }

    private void showSongOrderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_song_sort);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            //点击的菜单与配置中的一样，则不需要排序
            if (AppSetting.getSongSortMode().getMenuId() == itemId) {
                return true;
            }
//            SongInfo playingSong = playList.getPlayingSong();
//            Log.d("MainPresenter", "Menu getId():" + playingSong.getId());
//            Log.d("MainPresenter",
//                    "Menu getAdapterPosition():" + playingSong.getAdapterPosition());
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
                    baseAdapter.sortByAddTime();
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
        //重置上一次选中的歌曲
        if (currentColorSong != null) {
            currentColorSong.setPlayListSelected(false);
        }
        if (song != null) {
            song.setPlayListSelected(true);
            baseAdapter.notifyDataSetChanged();
            currentColorSong = song;
        }
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return baseAdapter.getLayoutManager();
    }

    @Override
    public void addSongs(List <SongInfo> newSongInfos) {
        List <SongInfo> songInfos = playList.getSongInfos();
        songInfos.addAll(newSongInfos);
        baseAdapter.setNewData(songInfos);
        SongModel.insertOrReplaceInTx(newSongInfos);
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
        if (currentColorSong != null) {
            baseAdapter.scrollToPosition(currentColorSong.getAdapterPosition());
        }
    }
}
