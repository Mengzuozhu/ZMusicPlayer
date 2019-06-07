package com.mzz.zmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.SearchView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.MusicSearchAdapter;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicSearchActivity extends BaseActivity {

    private static final String EXTRA_SEARCH_DATA = "com.mzz.zmusicplayer.EXTRA_SEARCH_DATA";
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    SearchView searchView;
    private MusicSearchAdapter musicSearchAdapter;

    public static void startForResult(FragmentActivity activity, Parcelable value) {
        Intent intent = new Intent(activity, MusicSearchActivity.class).putExtra(EXTRA_SEARCH_DATA,
                value);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        PlayList playList = getIntent().getParcelableExtra(EXTRA_SEARCH_DATA);
        //重置选中歌曲的颜色，避免出现多个选中歌曲
        List <SongInfo> songInfos = playList.getSongs();
        for (SongInfo songInfo : songInfos) {
            if (songInfo.isPlayListSelected()) {
                songInfo.setPlayListSelected(false);
                break;
            }
        }
        musicSearchAdapter = new MusicSearchAdapter(playList, rvSearch);
        musicSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            SongInfo song = musicSearchAdapter.getItem(position);
            EventBus.getDefault().post(song);
            musicSearchAdapter.updatePlaySongBackgroundColor(song);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //通过MenuItem得到SearchView
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint("搜索");
        musicSearchAdapter.setQueryTextListener(searchView);
        return super.onCreateOptionsMenu(menu);
    }
}
