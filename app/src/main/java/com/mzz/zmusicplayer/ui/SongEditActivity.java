package com.mzz.zmusicplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.edit.EditHandler;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongEditActivity extends BaseActivity {

    public static final String EDIT_DATA = "EDIT_DATA";
    public static final String DELETE_NUM = "DELETE_NUM";
    public static final int EDIT_SAVE = 3;
    @BindView(R.id.rv_edit)
    RecyclerView rvEdit;
    EditHandler <SongInfo> editHandler;

    /**
     * Start for result.
     *
     * @param activity the activity
     * @param value    the value
     */
    public static void startForResult(FragmentActivity activity, ArrayList <?
            extends Parcelable> value) {
        Intent intent =
                new Intent(activity, SongEditActivity.class).putParcelableArrayListExtra(EDIT_DATA, value);
        activity.startActivityForResult(intent, EDIT_SAVE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_edit);
        ButterKnife.bind(this);

        ArrayList <SongInfo> editData = getParcelableArrayListExtra(EDIT_DATA);
        init(editData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        getMenuInflater().inflate(R.menu.menu_delete_all, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            save();
            this.finish();
            return true;
        } else if (itemId == R.id.action_delete_all) {
            editHandler.deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(ArrayList <SongInfo> editData) {
        BaseQuickAdapter adapter =
                new BaseQuickAdapter <SongInfo, BaseViewHolder>(R.layout.item_song_edit,
                        editData) {
                    @Override
                    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
                        helper.setText(R.id.tv_edit_item_song_name, songInfo.getName());
                        helper.setText(R.id.tv_edit_item_song_artist, songInfo.getArtist());
                        helper.setText(R.id.tv_edit_item_song_num,
                                String.valueOf(helper.getAdapterPosition()));
                        helper.addOnClickListener(R.id.iv_edit_del);
                    }
                };
        rvEdit.setLayoutManager(new LinearLayoutManager(this));
        rvEdit.setAdapter(adapter);
        adapter.setHeaderView(getEmptyDummyHeader(this));
        editHandler = new EditHandler <>(this, editData, adapter).setOnItemChildDeleteListener();
    }

    private View getEmptyDummyHeader(Context context) {
        //为使得头部一致，设置一个空头部
        return LayoutInflater.from(context).inflate(R.layout.content_empty, rvEdit, false);
    }

    private void save() {
        Intent intent = getIntent().putIntegerArrayListExtra(DELETE_NUM,
                editHandler.getDeleteIds());
        setResult(EDIT_SAVE, intent);
    }

}
