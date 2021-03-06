package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.view.contract.AppSettingContract;
import com.mzz.zmusicplayer.view.presenter.AppSettingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppSettingActivity extends BaseActivity implements AppSettingContract.View {

    @BindView(R.id.rv_app_setting)
    RecyclerView rvAppSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        ButterKnife.bind(this);

        AppSettingContract.Presenter presenter = new AppSettingPresenter(this);
        presenter.initSetting();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvAppSetting;
    }

}
