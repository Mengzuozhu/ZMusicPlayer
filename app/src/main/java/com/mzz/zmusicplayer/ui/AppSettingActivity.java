package com.mzz.zmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.contract.AppSettingContract;
import com.mzz.zmusicplayer.presenter.AppSettingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppSettingActivity extends BaseActivity implements AppSettingContract.View {

    @BindView(R.id.rv_app_setting)
    RecyclerView rvAppSetting;
    private AppSettingContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        ButterKnife.bind(this);

        presenter = new AppSettingPresenter(this);
        presenter.initSetting();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvAppSetting;
    }

    @Override
    public void showCityPickerActivity() {
        CityPickerActivity.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CityPickerActivity.CITY_PICKER_CODE) {
            presenter.initSetting();
        }
    }

}
