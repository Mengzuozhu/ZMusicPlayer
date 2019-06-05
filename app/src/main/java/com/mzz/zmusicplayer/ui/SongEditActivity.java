package com.mzz.zmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.adapter.SongEditAdapter;
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
    @BindView(R.id.sv_edit)
    SearchView svEdit;
    private EditHandler <SongInfo> editHandler;

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
        SongEditAdapter adapter = new SongEditAdapter(rvEdit, editData);
        adapter.setQueryTextListener(svEdit);
        editHandler = new EditHandler <>(this, editData, adapter).setOnItemChildDeleteListener();
    }

    private void save() {
        Intent intent = getIntent().putIntegerArrayListExtra(DELETE_NUM,
                editHandler.getDeleteIds());
        setResult(EDIT_SAVE, intent);
    }

}
