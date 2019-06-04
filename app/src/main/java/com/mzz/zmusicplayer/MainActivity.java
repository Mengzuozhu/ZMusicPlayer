package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.edit.EditHandler;
import com.mzz.zmusicplayer.receiver.HeadsetReceiver;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.LocalMusicFragment;
import com.mzz.zmusicplayer.ui.MusicControlFragment;
import com.mzz.zmusicplayer.ui.RecentFragment;
import com.mzz.zmusicplayer.ui.SongEditActivity;
import com.mzz.zmusicplayer.ui.SongPickerActivity;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements LocalMusicFragment.LocalMusicListener,
        MusicControlFragment.MusicControlListener {

    private static final String[] CONTENT = new String[]{"最近", "本地"};
    @BindView(R.id.layout_drawer)
    DrawerLayout layoutDrawer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    private RecentFragment recentFragment;
    private LocalMusicFragment localMusicFragment;
    private Fragment[] fragments;
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
        initTabPage();
        playedMode = AppSetting.getPlayMode();
        initMenu();
        initNavigationView();
        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, intentFilter);
    }

    private void initTabPage() {
        localMusicFragment = LocalMusicFragment.newInstance();
        recentFragment = RecentFragment.newInstance();
        fragments = new Fragment[CONTENT.length];
        fragments[0] = recentFragment;
        fragments[1] = localMusicFragment;
        FragmentPagerAdapter adapter =
                new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(1);
    }

    private void initNavigationView() {
        ColorStateList csl = this.getColorStateList(R.color.colorWhite);
        navView.setItemTextColor(csl);
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_scan) {
                startActivityForResult(new Intent(this, SongPickerActivity.class),
                        SongPickerActivity.ADD_SONG_CODE);
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
        localMusicFragment.updateSongCountAndMode();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == SongPickerActivity.ADD_SONG_CODE) {
            ArrayList <SongInfo> newSongInfos =
                    data.getParcelableArrayListExtra(SongPickerActivity.ADD_SONG);
            localMusicFragment.addSongs(newSongInfos);
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
        localMusicFragment.deleteByKeyInTx(ids);
    }

    @Override
    public void updatePlaySongBackgroundColor(SongInfo song) {
        localMusicFragment.updatePlaySongBackgroundColor(song);
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

}
