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
import com.mzz.zmusicplayer.adapter.SongQueryAdapter;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicSearchActivity extends BaseActivity {

    public static final String SEARCH_DATA = "SEARCH_DATA";
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    SearchView mSearchView;
    private SongQueryAdapter queryAdapter;

    public static void startForResult(FragmentActivity activity,
                                      ArrayList <? extends Parcelable> value) {
        Intent intent =
                new Intent(activity, MusicSearchActivity.class).putParcelableArrayListExtra(SEARCH_DATA,
                        value);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        List <SongInfo> songInfos = getIntent().getParcelableArrayListExtra(SEARCH_DATA);
        queryAdapter = new SongQueryAdapter(songInfos, rvSearch,
                this, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.onActionViewExpanded();
        mSearchView.setQueryHint("搜索");
        queryAdapter.setQueryTextListener(mSearchView, this.getColor(R.color.colorGreen));
        return super.onCreateOptionsMenu(menu);
    }
}
