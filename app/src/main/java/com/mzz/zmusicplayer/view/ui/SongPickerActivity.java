package com.mzz.zmusicplayer.view.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.databinding.ActivitySongPickerBinding;
import com.mzz.zmusicplayer.manage.FileManager;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongPickerAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SongPickerActivity extends BaseActivity {

    public static final int CODE_ADD_SONG = 5;
    private static final int SDK_ANDROID_13 = 33;
    private static final String PERMISSION_READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String EXTRA_ADD_SONG = "com.mzz.zmusicplayer.EXTRA_ADD_SONG";

    private ActivitySongPickerBinding binding;
    private SongPickerAdapter songPickerAdapter;
    private List<SongInfo> songInfos;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fabSongFileScrollFirst.setOnClickListener(v -> songPickerAdapter.scrollToFirst());

        RxPermissions rxPermissions = new RxPermissions(this);
        String permission = Build.VERSION.SDK_INT >= SDK_ANDROID_13
                ? PERMISSION_READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        rxPermissions.request(permission).subscribe(granted -> {
            if (granted) {
                initAdapter();
            } else {
                showToast("无权限访问本地音乐");
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void initAdapter() {
        Set<Integer> allSongIdInFile = LocalSong.getInstance().getAllSongIdInFile();
        songInfos = FileManager.getInstance().getAllSongInfos(allSongIdInFile);
        songPickerAdapter = new SongPickerAdapter(songInfos, binding.contentSongPicker.rvSongFile);
        songPickerAdapter.setQueryTextListener(binding.contentSongPicker.svSongFile);
        songPickerAdapter.setScrollFirstShowInNeed(binding.fabSongFileScrollFirst);
        initHeader();
    }

    private void initHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.content_song_picker_header,
                binding.contentSongPicker.rvSongFile, false);
        TextView tvCount = header.findViewById(R.id.tv_picker_header_count);
        tvCount.setText(StringHelper.getLocalFormat("%d首", songInfos.size()));

        CheckBox chbSelectAll = header.findViewById(R.id.chb_picker_select_all);
        chbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> songPickerAdapter.selectAll(isChecked));

        ImageView ivSort = header.findViewById(R.id.iv_picker_header_sort);
        ivSort.setOnClickListener(v -> showSongOrderPopupMenu(ivSort));
        songPickerAdapter.setHeaderView(header);
    }

    private void showSongOrderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_sort_by_name);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_sort_ascend_by_name) {
                songPickerAdapter.sortByName(true);
                return true;
            } else if (itemId == R.id.action_sort_descend_by_name) {
                songPickerAdapter.sortByName(false);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void save() {
        ArrayList<SongInfo> newSongInfos = getCheckedSongInfos();
        Intent intent = getIntent().putParcelableArrayListExtra(EXTRA_ADD_SONG, newSongInfos);
        setResult(CODE_ADD_SONG, intent);
    }

    private ArrayList<SongInfo> getCheckedSongInfos() {
        ArrayList<SongInfo> checkedSongs = new ArrayList<>();
        for (SongInfo songInfo : songInfos) {
            if (songInfo.getIsChecked()) {
                checkedSongs.add(songInfo);
            }
        }
        return checkedSongs;
    }
}
