package com.mzz.zmusicplayer.presenter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.MultipleItem;
import com.mzz.zmusicplayer.adapter.MultipleItemQuickAdapter;
import com.mzz.zmusicplayer.contract.AppSettingContract;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.song.RecentSong;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/10 15:22
 * description :
 */
public class AppSettingPresenter implements AppSettingContract.Presenter {
    private static final String CANCEL_NAME = "取消";
    private static final String CONFIRM = "确定";
    private static final String RECENT_SONG_COUNT_NAME = "最近歌曲上限";
    private int recentSongCount;
    private RecyclerView recyclerView;
    private Context context;

    public AppSettingPresenter(AppSettingContract.View view) {
        recyclerView = view.getRecyclerView();
        context = recyclerView.getContext();
    }

    @Override
    public void initSetting() {
        List<MultipleItem> settings = getSettings();
        MultipleItemQuickAdapter multipleItemAdapter = new MultipleItemQuickAdapter(settings,
                recyclerView);
        multipleItemAdapter.setOnItemClickListener((adapter1, view1, position) -> {
            TextView textView = view1.findViewById(R.id.tv_setting_value);
            showSetting(position, textView);
        });
    }

    private List<MultipleItem> getSettings() {
        List<MultipleItem> settings = new ArrayList<>();
        recentSongCount = AppSetting.getRecentSongMaxCount();
        settings.add(new MultipleItem(MultipleItem.RIGHT_BUTTON, RECENT_SONG_COUNT_NAME,
                getRecentSongCountInfo()));
        return settings;
    }

    private String getRecentSongCountInfo() {
        return StringHelper.getLocalFormat("%d首", recentSongCount);
    }

    private void showSetting(int position, TextView textView) {
        if (position == 0) {
            showRecentCountDialog(textView);
        }
    }

    private void showRecentCountDialog(TextView textView) {
        NumberPicker numberPicker = getNumberPicker(RecentSong.getRecentCountValues(),
                (recentSongCount / 10) - 1);
        AlertDialog alertDialog = getAlertDialog(RECENT_SONG_COUNT_NAME)
                .setView(numberPicker).setPositiveButton(CONFIRM, (dialog, which) -> {
                    recentSongCount = (numberPicker.getValue() + 1) * 10;
                    textView.setText(getRecentSongCountInfo());
                    RecentSong.getInstance().updateRecentMaxCount(recentSongCount);
                    dialog.dismiss();
                })
                .create();
        alertDialog.show();
    }

    private AlertDialog.Builder getAlertDialog(String title) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setNegativeButton(CANCEL_NAME, (dialog, which) -> dialog.dismiss());
    }

    private NumberPicker getNumberPicker(String[] displayedValues, int value) {
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length - 1);
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setValue(value);
        return numberPicker;
    }

}
