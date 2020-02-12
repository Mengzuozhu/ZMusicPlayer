package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.mzz.zandroidcommon.common.EventBusHelper;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.adapter.MusicPage;
import com.mzz.zmusicplayer.adapter.MusicPagerAdapter;
import com.mzz.zmusicplayer.edit.EditHandler;
import com.mzz.zmusicplayer.edit.RemovedSongInfo;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.receiver.HeadsetReceiver;
import com.mzz.zmusicplayer.song.ISongChangeListener;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.AppSettingActivity;
import com.mzz.zmusicplayer.ui.FavoriteFragment;
import com.mzz.zmusicplayer.ui.LocalSongFragment;
import com.mzz.zmusicplayer.ui.MusicControlFragment;
import com.mzz.zmusicplayer.ui.PlayListFragment;
import com.mzz.zmusicplayer.ui.RecentFragment;
import com.mzz.zmusicplayer.ui.SongEditActivity;
import com.mzz.zmusicplayer.ui.SongPickerActivity;
import com.tencent.bugly.crashreport.CrashReport;
import com.viewpagerindicator.TabPageIndicator;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements PlayListFragment.PlayListListener {

    List<ISongChangeListener> songChangeListeners;
    private RecentFragment recentFragment;
    private LocalSongFragment localSongFragment;
    private PlayListFragment playListFragment;
    private MusicControlFragment musicControlFragment;
    private HeadsetReceiver headsetReceiver;
    private FavoriteFragment favoriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ViewerHelper.displayHomeAsUpOrNot(this.getSupportActionBar(), false);

        init();
        EventBusHelper.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);

        if (headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_song_add) {
            startActivityForResult(new Intent(this, SongPickerActivity.class),
                    SongPickerActivity.CODE_ADD_SONG);
        } else if (itemId == R.id.action_app_setting) {
            openActivity(AppSettingActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initTabPage();
        initSongChangeListeners();
        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, intentFilter);
    }

    private void initSongChangeListeners() {
        songChangeListeners = new ArrayList<>();
        songChangeListeners.add(recentFragment);
        songChangeListeners.add(localSongFragment);
        songChangeListeners.add(favoriteFragment);
    }

    private void initTabPage() {
        recentFragment = RecentFragment.newInstance();
        playListFragment = PlayListFragment.newInstance();
        favoriteFragment = FavoriteFragment.newInstance();
        localSongFragment = LocalSongFragment.newInstance();
        List<MusicPage> fragments = new ArrayList<>();
        fragments.add(new MusicPage(playListFragment, "播放"));
        fragments.add(new MusicPage(recentFragment, "最近"));
        fragments.add(new MusicPage(localSongFragment, "本地"));
        fragments.add(new MusicPage(favoriteFragment, "喜欢"));
        FragmentPagerAdapter adapter =
                new MusicPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        //保证播放界面不被销毁
        pager.setOffscreenPageLimit(4);
        TabPageIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
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
    public void setPlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        if (musicControlFragment == null) {
            musicControlFragment = MusicControlFragment.newInstance(playList);
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_control,
                    musicControlFragment).commit();
        } else {
            musicControlFragment.updateControlPlayList(playList);
        }
    }

    @Override
    public void setPlayingIndex(int playingIndex) {
        if (musicControlFragment != null) {
            musicControlFragment.updatePlayingIndex(playingIndex);
        } else {
            setPlayList(new PlayList());
        }
    }

    @Subscribe
    public void updatePlayingSong(SongInfo songInfo) {
        if (musicControlFragment != null) {
            musicControlFragment.updatePlayingSong(songInfo);
            if (songChangeListeners != null) {
                for (ISongChangeListener songChangeListener : songChangeListeners) {
                    songChangeListener.updatePlaySongBackgroundColor(songInfo);
                }
            }
        } else {
            setPlayList(new PlayList());
        }
    }

    @Subscribe
    public void updatePlayListSongs(List<SongInfo> songInfos) {
        playListFragment.updatePlayListSongs(songInfos);
    }

    @Subscribe
    public void removeSong(RemovedSongInfo removedSongInfo) {
        SongInfo removedSong = removedSongInfo.getSongInfo();
        switch (removedSongInfo.getSongListType()) {
            case PLAYLIST:
                playListFragment.remove(removedSong);
                break;
            case RECENT:
                recentFragment.removeSong(removedSong);
                break;
            case LOCAL:
                localSongFragment.removeSong(removedSong);
                break;
            case FAVORITE:
                favoriteFragment.removeSong(removedSong);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == SongPickerActivity.CODE_ADD_SONG) {
            ArrayList<SongInfo> newSongInfos =
                    data.getParcelableArrayListExtra(SongPickerActivity.EXTRA_ADD_SONG);
            localSongFragment.addToLocalSongs(newSongInfos);
        } else if (resultCode == SongListType.PLAYLIST.getCode()) {
            List<Long> deleteIds = getDeleteIds(data);
            playListFragment.remove(deleteIds);
        } else if (resultCode == SongListType.RECENT.getCode()) {
            List<Long> deleteIds = getDeleteIds(data);
            recentFragment.remove(deleteIds);
        } else if (resultCode == SongListType.LOCAL.getCode()) {
            List<Long> deleteIds = getDeleteIds(data);
            localSongFragment.remove(deleteIds);
        } else if (resultCode == SongListType.FAVORITE.getCode()) {
            List<Long> deleteIds = getDeleteIds(data);
            favoriteFragment.remove(deleteIds);
        }
    }

    private List<Long> getDeleteIds(Intent data) {
        ArrayList<Integer> deleteIds =
                data.getIntegerArrayListExtra(SongEditActivity.EXTRA_DELETE_ID);
        if (deleteIds == null) {
            return new ArrayList<>();
        }
        return EditHandler.integerToLongList(deleteIds);
    }

}
