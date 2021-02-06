package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.common.collect.ImmutableMap;
import com.mzz.zandroidcommon.common.EventBusHelper;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.enums.SongListType;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.receiver.HeadsetReceiver;
import com.mzz.zmusicplayer.song.ISongChangeListener;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.MusicPage;
import com.mzz.zmusicplayer.view.adapter.MusicPagerAdapter;
import com.mzz.zmusicplayer.view.edit.RemovedSongInfo;
import com.mzz.zmusicplayer.view.ui.AppSettingActivity;
import com.mzz.zmusicplayer.view.ui.FavoriteFragment;
import com.mzz.zmusicplayer.view.ui.LocalSongFragment;
import com.mzz.zmusicplayer.view.ui.MusicControlFragment;
import com.mzz.zmusicplayer.view.ui.PlayListFragment;
import com.mzz.zmusicplayer.view.ui.PlayListFragment.PlayListListener;
import com.mzz.zmusicplayer.view.ui.RecentFragment;
import com.mzz.zmusicplayer.view.ui.SongEditActivity;
import com.mzz.zmusicplayer.view.ui.SongFragment;
import com.mzz.zmusicplayer.view.ui.SongPickerActivity;
import com.viewpagerindicator.TabPageIndicator;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.ButterKnife;

/**
 * @author Mengzz
 */
public class MainActivity extends BaseActivity implements PlayListListener {

    private List<ISongChangeListener> songChangeListeners;
    private RecentFragment recentFragment;
    private LocalSongFragment localSongFragment;
    private PlayListFragment playListFragment;
    private MusicControlFragment musicControlFragment;
    private FavoriteFragment favoriteFragment;
    private HeadsetReceiver headsetReceiver;
    private Map<SongListType, SongFragment> fragmentMap;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void setPlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        if (musicControlFragment == null) {
            musicControlFragment = MusicControlFragment.newInstance(playList);
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_control, musicControlFragment).commit();
        } else {
            musicControlFragment.updateControlPlayList(playList);
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
        SongFragment songFragment = fragmentMap.get(removedSongInfo.getSongListType());
        if (songFragment != null) {
            songFragment.removeSong(removedSong);
        }
    }

    @Override
    public void onBackPressed() {
        //返回键，不退出程序
        moveTaskToBack(true);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);

        if (headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == SongPickerActivity.CODE_ADD_SONG) {
            List<SongInfo> newSongInfos = data.getParcelableArrayListExtra(SongPickerActivity.EXTRA_ADD_SONG);
            localSongFragment.addToLocalSongs(newSongInfos);
        } else {
            removeSongByIds(resultCode, data);
        }
    }

    private void removeSongByIds(int resultCode, Intent data) {
        SongFragment fragment = fragmentMap.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getCode(), Map.Entry::getValue))
                .get(resultCode);
        if (fragment != null) {
            fragment.remove(getDeleteIds(data));
        }
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
        fragmentMap = ImmutableMap.<SongListType, SongFragment>builder()
                .put(SongListType.PLAYLIST, playListFragment)
                .put(SongListType.RECENT, recentFragment)
                .put(SongListType.LOCAL, localSongFragment)
                .put(SongListType.FAVORITE, favoriteFragment)
                .build();
        List<MusicPage> fragments = fragmentMap.entrySet()
                .stream()
                .map(entry -> new MusicPage(entry.getValue(), entry.getKey().getDesc()))
                .collect(Collectors.toList());
        FragmentPagerAdapter adapter = new MusicPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        //保证播放界面不被销毁
        pager.setOffscreenPageLimit(4);
        TabPageIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    private List<Long> getDeleteIds(Intent data) {
        List<Integer> deleteIds = data.getIntegerArrayListExtra(SongEditActivity.EXTRA_DELETE_ID);
        if (deleteIds == null) {
            return new ArrayList<>();
        }
        return deleteIds.stream().map(Integer::longValue).collect(Collectors.toList());
    }

}
