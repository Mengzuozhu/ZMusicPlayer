package com.mzz.zmusicplayer.presenter;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongInfoAdapter;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.model.SongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
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

    private RecyclerView recyclerView;
    private PlayList playList;
    private SongInfoAdapter baseAdapter;
    private int selectColor;
    private MainContract.View mView;
    private FragmentActivity context;
    private int[] textViewIds = new int[]{R.id.tv_item_song_name, R.id.tv_item_song_artist,
            R.id.tv_item_song_num};
    private SongInfo currentColorSong;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        context = mView.getActivity();
        recyclerView = mView.getRecyclerView();
        selectColor = context.getColor(R.color.colorGreen);
        initSongInfos();
        intiAdapter();
        mView.updateControlFragment(playList);
        //设置缓存大小，避免多个item出现选中颜色
        recyclerView.setItemViewCacheSize(playList.getSongInfos().size());
    }

    private void initSongInfos() {
        List <SongInfo> songInfos = SongModel.getOrderSongInfos();
        playList = new PlayList(songInfos, AppSetting.getLastPlaySongIndex(),
                AppSetting.getPlayMode());
    }

    private void intiAdapter() {
        baseAdapter = new SongInfoAdapter(playList.getSongInfos(), recyclerView,
                context, false);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            playList.setPlayingIndex(position);
            mView.updateControlFragment(playList);
        });
        setHeader();
    }

    private void setHeader() {
        View header = LayoutInflater.from(context).inflate(R.layout.content_song_header,
                recyclerView, false);
        ImageView searchView = header.findViewById(R.id.iv_header_search);
        searchView.setOnClickListener(v -> showSearchActivity());
        ImageView sortView = header.findViewById(R.id.iv_header_sort);
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        baseAdapter.setHeaderView(header);
        //隐藏头部
//        scrollToPosition(1);
    }

    private void showSearchActivity() {
        SearchActivity.startForResult(context, (ArrayList <SongInfo>) playList.getSongInfos());
    }

    private void showSongOrderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_song_sort);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
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
    public void setPlaySongBackgroundColor(SongInfo song) {
        if (currentColorSong != null) {
            int lastPosition = currentColorSong.getAdapterPosition();
            setTextViewBackground(lastPosition, Color.WHITE);
            setDivider(lastPosition, Color.TRANSPARENT);
        }
        int position = song.getAdapterPosition();
        setTextViewBackground(position, selectColor);
        setDivider(position, selectColor);
        currentColorSong = song;
    }

    private void setTextViewBackground(int position, int color) {
        for (int viewId : textViewIds) {
            TextView textView = (TextView) baseAdapter.getViewByPosition(recyclerView, position,
                    viewId);
            if (textView != null) {
                textView.setTextColor(color);
            }
        }
    }

    private void setDivider(int position, int color) {
        View subView = baseAdapter.getViewByPosition(recyclerView, position, R.id.divider_item);
        if (subView != null) {
            subView.setBackgroundColor(color);
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
}
