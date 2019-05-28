package com.mzz.zmusicplayer.presenter;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.song.FileManager;
import com.mzz.zmusicplayer.song.SongInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/28 17:50
 * description :
 */
public class MainPresenter implements MainContract.Presenter {

    private int selectColor;
    private MainContract.View mView;
    private FragmentActivity context;
    private List <SongInfo> songFiles;
    private int itemSongNameId = R.id.tv_item_song_name;
    private int itemSongArtistId = R.id.tv_item_song_artist;
    private int[] TextViewIds = new int[]{itemSongNameId, itemSongArtistId};
    private View lastView;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        context = mView.getActivity();
        selectColor = context.getColor(R.color.colorGreen);
        initSongInfos();
        SongInfo songInfo = songFiles.get(0);
        if (songInfo != null) {
            mView.setControlFragment(songInfo);
        }
        intiAdapter();
    }

    private void initSongInfos() {
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                songFiles = FileManager.getInstance(context).getSongInfos();
            } else {
                songFiles = new ArrayList <>();
                ViewerHelper.showToast(context, "无权限访问");
            }
        });
    }

    private void intiAdapter() {
        BaseQuickAdapter baseQuickAdapter =
                new BaseQuickAdapter <SongInfo, BaseViewHolder>(R.layout.item_song_list,
                        songFiles) {
                    @Override
                    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
                        helper.setText(itemSongNameId, songInfo.getName());
                        helper.setText(itemSongArtistId, songInfo.getArtist());
                    }
                };
        baseQuickAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo songInfo = (SongInfo) adapter.getItem(position);
            if (songInfo != null) {
                mView.setControlFragment(songInfo);
                setSongBackgroundColor(view);
            }
        });
        RecyclerView recyclerView = mView.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(baseQuickAdapter);

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
        for (int songId : TextViewIds) {
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

}
