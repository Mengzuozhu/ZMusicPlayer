package com.mzz.zmusicplayer.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongInfoAdapter;
import com.mzz.zmusicplayer.song.FileManager;
import com.mzz.zmusicplayer.song.SongInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongPickerActivity extends BaseActivity {

    public static final int ADD_SONG_CODE = 5;
    public static final String ADD_SONG = "ADD_SONG";

    @BindView(R.id.rv_song_file)
    RecyclerView rvSongFile;
    @BindView(R.id.sv_song_file)
    SearchView svSongFile;
    @BindView(R.id.fab_song_file_scroll_first)
    FloatingActionButton fabSongScrollFirst;
    SongInfoAdapter adapter;
    List <SongInfo> songInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_picker);
        ButterKnife.bind(this);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                initAlarmSong();
            } else {
                showToast("无权限访问");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        getMenuInflater().inflate(R.menu.menu_select_all, menu);
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_select_all:
                adapter.selectAll();
                break;
            case R.id.action_save:
                save();
                this.finish();
                return true;
            case R.id.action_sort_ascend:
                adapter.sortByName(true);
                break;
            case R.id.action_sort_descend:
                adapter.sortByName(false);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initAlarmSong() {
        songInfos = FileManager.getInstance(SongPickerActivity.this).getSongInfos();
        adapter = new SongInfoAdapter(songInfos, rvSongFile, this, true);
        adapter.setQueryTextListener(svSongFile, this.getColor(R.color.colorGreen));
        ViewerHelper.showOrHideScrollFirst(rvSongFile, adapter.getLayoutManager(),
                fabSongScrollFirst);
    }

    private void save() {
        ArrayList <SongInfo> newSongInfos = getCheckedSongInfos();
        Intent intent = getIntent().putParcelableArrayListExtra(ADD_SONG, newSongInfos);
        setResult(ADD_SONG_CODE, intent);
    }

    private ArrayList <SongInfo> getCheckedSongInfos() {
        ArrayList <SongInfo> checkedSongs = new ArrayList <>();
        for (SongInfo songFile : songInfos) {
            if (songFile.getIsChecked()) {
                checkedSongs.add(songFile);
            }
        }
        return checkedSongs;
    }

    @OnClick(R.id.fab_song_file_scroll_first)
    public void scrollToFirstSongOnClick(View view) {
        adapter.scrollToPosition(0);
    }
}
