package com.mzz.zmusicplayer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.presenter.MainPresenter;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.song.SongPickerActivity;

import java.util.ArrayList;

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
    MainContract.Presenter mainPresenter;
    ControlFragment controlFragment;
    PlayedMode playedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        mainPresenter = new MainPresenter(this);
        ViewerHelper.showOrHideScrollFirst(rvSong, mainPresenter.getLayoutManager(),
                fabSongScrollFirst);
        playedMode = AppSetting.getLastPlayMode(this);
        initMenu();
        initNavigationView();
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
                playedMode = playedMode.getNextMode();
                item.setTitle(playedMode.getDesc());
                AppSetting.setPlayMode(this, playedMode);
                controlFragment.setPlayMode(playedMode);
            }
            return true;
        });
    }

    private void initMenu() {
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_play_mode);
        menuItem.setTitle(playedMode.getDesc());
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
    public void setControlFragment(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        if (controlFragment == null) {
            controlFragment = ControlFragment.newInstance(playList);
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_control,
                    controlFragment).commit();
        } else {
            controlFragment.setPlayList(playList);
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
        }
    }

    @OnClick(R.id.fab_song_scroll_first)
    public void scrollToFirstSongOnClick(View view) {
        mainPresenter.scrollToFirst();
    }

}
