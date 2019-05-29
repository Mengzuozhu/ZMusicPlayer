package com.mzz.zmusicplayer.presenter;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongInfoAdapter;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.model.SongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

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
    private int itemSongNameId = R.id.tv_item_song_name;
    private int itemSongArtistId = R.id.tv_item_song_artist;
    private int[] textViewIds = new int[]{itemSongNameId, itemSongArtistId};
    private int lastViewPosition = -1;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        context = mView.getActivity();
        recyclerView = mView.getRecyclerView();
        selectColor = context.getColor(R.color.colorGreen);
        initSongInfos();
        intiAdapter();
        mView.updateControlFragment(playList);
    }

    private void initSongInfos() {
        List <SongInfo> songInfos = SongModel.getSortedSongInfos();
        playList = new PlayList(songInfos, AppSetting.getLastPlaySongIndex(context),
                AppSetting.getLastPlayMode(context));
    }

    private void intiAdapter() {
        baseAdapter = new SongInfoAdapter(playList.getSongInfos(), recyclerView,
                context);
//        baseAdapter.setQueryTextListener(svSongFile);

        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            playList.setPlayingIndex(position);
            mView.updateControlFragment(playList);
            setPlaySongBackgroundColor(position);
        });
    }

    public void setPlaySongBackgroundColor(int position) {
        if (lastViewPosition != -1) {
            setTextViewBackground(lastViewPosition, Color.WHITE);
            setDivider(lastViewPosition, Color.TRANSPARENT);
        }
        setTextViewBackground(position, selectColor);
        setDivider(position, selectColor);
        lastViewPosition = position;
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
        if (baseAdapter.getItemCount() > 0) {
            mView.getRecyclerView().scrollToPosition(0);
        }
    }
}
