package com.mzz.zmusicplayer.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import androidx.fragment.app.FragmentActivity;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.databinding.ActivitySearchBinding;
import com.mzz.zmusicplayer.manage.AdapterManager;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.MusicSearchAdapter;

public class MusicSearchActivity extends BaseActivity {

    private static final String SEARCH = "搜索";
    private static final String EXTRA_PLAY_LIST = "com.mzz.zmusicplayer.EXTRA_PLAY_LIST";
    private ActivitySearchBinding binding;
    private MusicSearchAdapter musicSearchAdapter;
    private PlayList playList;

    public static void startForResult(FragmentActivity activity, PlayList value) {
        activity.startActivity(new Intent(activity, MusicSearchActivity.class)
                .putExtra(EXTRA_PLAY_LIST, value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        if (musicSearchAdapter == null) {
            return super.onCreateOptionsMenu(menu);
        }
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint(SEARCH);
        musicSearchAdapter.setQueryTextListener(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicSearchAdapter != null) {
            AdapterManager.unregister(musicSearchAdapter);
        }
        binding = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        playList = getIntent().getParcelableExtra(EXTRA_PLAY_LIST);
        if (playList == null) {
            finish();
            return;
        }
        SongInfo playingSong = playList.getPlayingSong();
        if (playingSong != null) {
            playingSong.setPlayListSelected(false);
        }
        musicSearchAdapter = new MusicSearchAdapter(playList, binding.rvSearch);
        AdapterManager.register(musicSearchAdapter);
    }
}
