package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.edit.EditHandler;
import com.mzz.zmusicplayer.presenter.MainPresenter;
import com.mzz.zmusicplayer.receiver.HeadsetReceiver;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.MusicControlFragment;
import com.mzz.zmusicplayer.ui.SongEditActivity;
import com.mzz.zmusicplayer.ui.SongPickerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.layout_drawer)
    DrawerLayout layoutDrawer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.fab_song_scroll_first)
    FloatingActionButton fabSongScrollFirst;
    private MainContract.Presenter mainPresenter;
    private MusicControlFragment musicControlFragment;
    private PlayedMode playedMode;
    private HeadsetReceiver headsetReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
    }

    private void init() {
        mainPresenter = new MainPresenter(this);
        musicControlFragment.setMainPresenter(mainPresenter);
        ViewerHelper.showOrHideScrollFirst(rvSong, mainPresenter.getLayoutManager(),
                fabSongScrollFirst);
        playedMode = AppSetting.getPlayMode();
        initMenu();
        initNavigationView();
        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, intentFilter);
    }

    private void initNavigationView() {
        ColorStateList csl = this.getColorStateList(R.color.colorWhite);
        navView.setItemTextColor(csl);
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_scan) {
                openActivityForResult(SongPickerActivity.class, SongPickerActivity.ADD_SONG_CODE);
                layoutDrawer.closeDrawers();
            } else if (itemId == R.id.nav_play_mode) {
                setPlayMode(item);
            }
            return true;
        });
    }

    private void setPlayMode(MenuItem item) {
        playedMode = playedMode.getNextMode();
        item.setTitle(playedMode.getDesc());
        AppSetting.setPlayMode(playedMode);
        musicControlFragment.setPlayMode(playedMode);
        mainPresenter.updateSongCountAndMode();
    }

    private void initMenu() {
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_play_mode);
        menuItem.setTitle(playedMode.getDesc());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //返回键，不退出程序
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (layoutDrawer.isDrawerOpen(GravityCompat.START)) {
                layoutDrawer.closeDrawers();
            } else {
                layoutDrawer.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        if (musicControlFragment == null) {
            musicControlFragment = MusicControlFragment.newInstance(playList);
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_control,
                    musicControlFragment).commit();
        } else {
            musicControlFragment.setPlayList(playList);
        }
    }

    @Override
    public void setPlayingIndex(int playingIndex) {
        if (musicControlFragment != null) {
            musicControlFragment.setPlayingIndex(playingIndex);
        } else {
            updatePlayList(new PlayList());
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvSong;
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == SongPickerActivity.ADD_SONG_CODE) {
            ArrayList <SongInfo> newSongInfos =
                    data.getParcelableArrayListExtra(SongPickerActivity.ADD_SONG);
            mainPresenter.addSongs(newSongInfos);
        } else if (resultCode == SongEditActivity.EDIT_SAVE) {
            onSaveEditEvent(data);
        }
    }

    public void onSaveEditEvent(Intent data) {
        ArrayList <Integer> deleteIds =
                data.getIntegerArrayListExtra(SongEditActivity.DELETE_NUM);
        if (deleteIds == null) {
            return;
        }
        List <Long> ids = EditHandler.integerToLongList(deleteIds);
        mainPresenter.deleteByKeyInTx(ids);
    }

    @OnClick(R.id.fab_song_scroll_first)
    public void scrollToFirstSongOnClick(View view) {
        mainPresenter.scrollToFirst();
    }

    @OnClick(R.id.fab_song_locate)
    public void locateToSelectedSongOnClick(View view) {
        mainPresenter.locateToSelectedSong();
    }

}
