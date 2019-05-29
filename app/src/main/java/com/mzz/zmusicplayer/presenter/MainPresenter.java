package com.mzz.zmusicplayer.presenter;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
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

    private PlayList playList;
    private SongInfoAdapter baseAdapter;
    private int selectColor;
    private MainContract.View mView;
    private FragmentActivity context;
    private int itemSongNameId = R.id.tv_item_song_name;
    private int itemSongArtistId = R.id.tv_item_song_artist;
    private int[] textViewIds = new int[]{itemSongNameId, itemSongArtistId};
    private View lastView;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        context = mView.getActivity();
        selectColor = context.getColor(R.color.colorGreen);
        initSongInfos();
        mView.setControlFragment(playList);
        intiAdapter();
    }

    private void initSongInfos() {
        List <SongInfo> songInfos = SongModel.getSortedSongInfos();
        AppSetting appSetting = AppSetting.readSetting(context);
        playList = new PlayList(songInfos, appSetting.getLastPlaySongIndex(),
                AppSetting.getLastPlayMode(context));
    }

    private void intiAdapter() {
        baseAdapter = new SongInfoAdapter(playList.getSongInfos(), mView.getRecyclerView(),
                context);
//        baseAdapter.setQueryTextListener(svSongFile);

        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            playList.setPlayingIndex(position);
            mView.setControlFragment(playList);
            setSongBackgroundColor(view);
//            SongInfo songInfo = (SongInfo) adapter.getItem(position);
//            if (songInfo != null) {
//                mView.setControlFragment(playList);
//                setSongBackgroundColor(view);
//            }
        });
    }

    private void setSongBackgroundColor(View view) {
        if (lastView != null) {
            setTextViewBackground(lastView, Color.WHITE);
            setDivider(lastView, Color.TRANSPARENT);
        }
        setTextViewBackground(view, selectColor);
        setDivider(view, selectColor);
        lastView = view;
    }

    private void setTextViewBackground(View view, int color) {
        for (int songId : textViewIds) {
            TextView textView = view.findViewById(songId);
            if (textView != null) {
                textView.setTextColor(color);
            }
        }
    }

    private void setDivider(View view, int color) {
        View subView = view.findViewById(R.id.divider_item);
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
