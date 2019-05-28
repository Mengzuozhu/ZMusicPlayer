package com.mzz.zmusicplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.presenter.MainPresenter;
import com.mzz.zmusicplayer.song.SongInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ViewerHelper.displayHomeAsUpOrNot(getSupportActionBar(), false);

        mainPresenter = new MainPresenter(this);
    }

    @Override
    public void setControlFragment(SongInfo songInfo) {
        ControlFragment controlFragment = ControlFragment.newInstance(songInfo);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_control,
                controlFragment).commit();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvSong;
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }
}
