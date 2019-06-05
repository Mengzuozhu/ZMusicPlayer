package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.adapter.MusicPagerAdapter;
import com.mzz.zmusicplayer.adapter.PageFragment;
import com.mzz.zmusicplayer.edit.EditHandler;
import com.mzz.zmusicplayer.receiver.HeadsetReceiver;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.ui.FavoriteFragment;
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

    @BindView(R.id.layout_drawer)
    DrawerLayout layoutDrawer;
    private LocalMusicFragment localMusicFragment;
    private MusicControlFragment musicControlFragment;
    private HeadsetReceiver headsetReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ViewerHelper.displayHomeAsUpOrNot(this.getSupportActionBar(), false);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (itemId == R.id.action_add_song) {
            startActivityForResult(new Intent(this, SongPickerActivity.class),
                    SongPickerActivity.ADD_SONG_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initTabPage();
        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, intentFilter);
    }

    private void initTabPage() {
        RecentFragment recentFragment = RecentFragment.newInstance();
        localMusicFragment = LocalMusicFragment.newInstance();
        FavoriteFragment favoriteFragment = FavoriteFragment.newInstance();
        List <PageFragment> fragments = new ArrayList <>();
        fragments.add(new PageFragment(recentFragment, "最近"));
        fragments.add(new PageFragment(localMusicFragment, "本地"));
        fragments.add(new PageFragment(favoriteFragment, "喜欢"));
        FragmentPagerAdapter adapter =
                new MusicPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(1);
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
            musicControlFragment.setPlayList(playList);
        }
    }

    @Override
    public void setPlayingIndex(int playingIndex) {
        if (musicControlFragment != null) {
            musicControlFragment.setPlayingIndex(playingIndex);
        } else {
            setPlayList(new PlayList());
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

    @Override
    public void updateSongCountAndMode() {
        localMusicFragment.updateSongCountAndMode();
    }

}
