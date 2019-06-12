package com.mzz.zmusicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.SearchView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.adapter.ICheckable;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TextQueryHandler;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * 支持歌曲查询的适配器
 * author : Mzz
 * date : 2019 2019/6/1 16:05
 * description :
 */
public class SongPickerAdapter extends SongInfoAdapter {

    private TextQueryHandler textQueryHandler;
    private int chbSongSelectId;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param songInfos    the song infos
     * @param recyclerView the recycler view
     */
    public SongPickerAdapter(List <SongInfo> songInfos, RecyclerView recyclerView) {
        super(R.layout.item_song_check, songInfos, recyclerView);
        chbSongSelectId = R.id.chb_item_song_select;
        ViewerHelper.setOnItemClickWithCheckBox(this, chbSongSelectId);
        textQueryHandler = new TextQueryHandler(this, recyclerView.getContext(),
                R.id.tv_item_song_name, R.id.tv_item_song_artist);
        setOnItemChildCheckListener();
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        helper.setChecked(chbSongSelectId, songInfo.getIsChecked()).addOnClickListener(chbSongSelectId);
        textQueryHandler.setTextByQueryResult(helper, songInfo);
    }

    private void setOnItemChildCheckListener() {
        this.setOnItemChildClickListener((adapter, view, position) -> {
            CheckBox checkBox = (CheckBox) view;
            ICheckable checkable = (ICheckable) adapter.getItem(position);
            if (checkable != null && checkBox != null) {
                checkable.setIsChecked(checkBox.isChecked());
            }

        });
    }

    /**
     * Select all.
     *
     * @param isChecked the is checked
     */
    public void selectAll(boolean isChecked) {
        for (int i = 0; i < this.getItemCount(); ++i) {
            CheckBox checkBox = (CheckBox) this.getViewByPosition(recyclerView, i,
                    this.getCheckableViewId());
            if (checkBox != null) {
                checkBox.setChecked(isChecked);
            }

            ICheckable checkable = this.getItem(i);
            if (checkable != null) {
                checkable.setIsChecked(isChecked);
            }
        }
    }

    private int getCheckableViewId() {
        return chbSongSelectId;
    }

    /**
     * Sets query text listener.
     *
     * @param searchView the search view
     */
    public void setQueryTextListener(SearchView searchView) {
        textQueryHandler.setQueryTextListener(searchView);
    }

}
