package com.mzz.zmusicplayer.view.edit;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import tyrantgit.explosionfield.ExplosionField;

/**
 * @author : Mzz
 * date : 2019 2019/5/26 16:06
 * description :
 */
public class EditHandler<T extends IEditItem> {
    @Getter
    private ArrayList<Integer> deleteIds = new ArrayList<>();
    private List<T> editData;
    private BaseQuickAdapter<T, BaseViewHolder> adapter;
    private ExplosionField explosionField;

    public EditHandler(Activity activity, List<T> editData, BaseQuickAdapter<T, BaseViewHolder> adapter) {
        this.editData = editData;
        this.adapter = adapter;
        explosionField = ExplosionField.attach2Window(activity);
    }

    /**
     * Delete all.
     */
    public void deleteAll() {
        for (T editItem : editData) {
            deleteIds.add(editItem.getId().intValue());
        }
        adapter.setNewData(new ArrayList<T>());
    }

    /**
     * Sets remove listener.
     */
    public EditHandler<T> setOnItemChildDeleteListener() {
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            IEditItem item = (IEditItem) adapter1.getItem(position);
            if (item == null) {
                return;
            }
            explosionField.explode(view);
            view.setOnClickListener(null);
            deleteIds.add(item.getId().intValue());
            adapter.remove(position);
//            adapter.notifyDataSetChanged();
        });
        return this;
    }
}
