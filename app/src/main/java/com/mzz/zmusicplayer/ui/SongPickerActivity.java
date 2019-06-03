package com.mzz.zmusicplayer.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongQueryAdapter;
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
    private SongQueryAdapter queryAdapter;
    private List <SongInfo> songInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_picker);
        ButterKnife.bind(this);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                initAdapter();
            } else {
                showToast("无权限访问");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            save();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        songInfos = FileManager.getInstance(SongPickerActivity.this).getSongInfos();
        queryAdapter = new SongQueryAdapter(songInfos, rvSongFile, this, true);
        queryAdapter.setQueryTextListener(svSongFile);
        ViewerHelper.showOrHideScrollFirst(rvSongFile, queryAdapter.getLayoutManager(),
                fabSongScrollFirst);
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.content_song_picker_header,
                rvSongFile, false);
        TextView tvCount = header.findViewById(R.id.tv_picker_header_count);
        tvCount.setText(StringHelper.getLocalFormat("%d首", songInfos.size()));

        CheckBox chbSelectAll = header.findViewById(R.id.chb_picker_select_all);
        chbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> queryAdapter.selectAll(isChecked));

        ImageView sortView = header.findViewById(R.id.iv_picker_header_sort);
        sortView.setOnClickListener(v -> showSongOrderPopupMenu(sortView));
        queryAdapter.setHeaderView(header);
    }

    private void showSongOrderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_sort_by_name);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            switch (itemId) {
                case R.id.action_sort_ascend_by_name:
                    queryAdapter.sortByName(true);
                    return true;
                case R.id.action_sort_descend_by_name:
                    queryAdapter.sortByName(false);
                    return true;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void save() {
        ArrayList <SongInfo> newSongInfos = getCheckedSongInfos();
        Intent intent = getIntent().putParcelableArrayListExtra(ADD_SONG, newSongInfos);
        setResult(ADD_SONG_CODE, intent);
    }

    private ArrayList <SongInfo> getCheckedSongInfos() {
        ArrayList <SongInfo> checkedSongs = new ArrayList <>();
        for (SongInfo songInfo : songInfos) {
            if (songInfo.getIsChecked()) {
                String pinyin = Pinyin.toPinyin(songInfo.getName(), "");
                if (pinyin == null) {
                    pinyin = songInfo.getName();
                }
                songInfo.setSpell(pinyin);
                checkedSongs.add(songInfo);
            }
        }
        return checkedSongs;
    }

    @OnClick(R.id.fab_song_file_scroll_first)
    public void scrollToFirstSongOnClick(View view) {
        queryAdapter.scrollToPosition(0);
    }
}
