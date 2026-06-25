package com.mzz.zmusicplayer.view.ui;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.databinding.ActivityAppSettingBinding;
import com.mzz.zmusicplayer.view.contract.AppSettingContract;
import com.mzz.zmusicplayer.view.presenter.AppSettingPresenter;

public class AppSettingActivity extends BaseActivity implements AppSettingContract.View {

    private ActivityAppSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppSettingContract.Presenter presenter = new AppSettingPresenter(this);
        presenter.initSetting();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.rvAppSetting;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
