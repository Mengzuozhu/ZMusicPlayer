package com.mzz.zmusicplayer.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;

import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.databinding.ActivitySongEditBinding;
import com.mzz.zmusicplayer.enums.SongListType;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.adapter.SongEditAdapter;
import com.mzz.zmusicplayer.view.edit.EditHandler;

import java.util.ArrayList;
import java.util.List;

public class SongEditActivity extends BaseActivity {

    public static final String EXTRA_DELETE_ID = "com.mzz.zmusicplayer.EXTRA_DELETE_ID";
    private static final String EXTRA_EDIT_DATA = "com.mzz.zmusicplayer.EXTRA_EDIT_DATA";
    private static final String EXTRA_RESULT_CODE = "com.mzz.zmusicplayer.EXTRA_RESULT_CODE";
    private ActivitySongEditBinding binding;
    private EditHandler<SongInfo> editHandler;

    public static void startForResult(FragmentActivity activity, List<SongInfo> value,
                                      SongListType songListType) {
        Intent intent =
                new Intent(activity, SongEditActivity.class).putParcelableArrayListExtra(EXTRA_EDIT_DATA,
                        (ArrayList<SongInfo>) value);
        int code = songListType.getCode();
        intent.putExtra(EXTRA_RESULT_CODE, code);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void init() {
        ArrayList<SongInfo> editData = getParcelableArrayListExtra(EXTRA_EDIT_DATA);
        SongEditAdapter adapter = new SongEditAdapter(binding.rvEdit, editData);
        adapter.setQueryTextListener(binding.svEdit);
        editHandler = new EditHandler<>(this, editData, adapter).setOnItemChildDeleteListener();
    }

    private void save() {
        Intent intent = getIntent();
        intent.putIntegerArrayListExtra(EXTRA_DELETE_ID, editHandler.getDeleteIds());
        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
        setResult(resultCode, intent);
    }

}
