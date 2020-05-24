package com.mzz.zmusicplayer.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.manage.AdapterManager;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.view.adapter.MusicSearchAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicSearchActivity extends BaseActivity {

    private static final String SEARCH = "搜索";
    private static PlayList playList;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    private MusicSearchAdapter musicSearchAdapter;

    /**
     * Start for result.
     *
     * @param activity the activity
     * @param value    the value
     */
    public static void startForResult(FragmentActivity activity, PlayList value) {
        playList = value;
        activity.startActivity(new Intent(activity, MusicSearchActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //通过MenuItem得到SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint(SEARCH);
        musicSearchAdapter.setQueryTextListener(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdapterManager.unregister(musicSearchAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (playList != null) {
            //重置选中歌曲的颜色，避免出现多个选中歌曲
            playList.getPlayingSong().setPlayListSelected(false);
            musicSearchAdapter = new MusicSearchAdapter(playList, rvSearch);
            AdapterManager.register(musicSearchAdapter);
        }
    }
}
