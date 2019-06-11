package com.mzz.zmusicplayer.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/5/11 16:48
 * description :
 */
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter <MultipleItem,
        BaseViewHolder> {

    public MultipleItemQuickAdapter(List<MultipleItem> data, RecyclerView recyclerView) {
        super(data);
        addItemType(MultipleItem.RIGHT_BUTTON, R.layout.item_setting);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(this);
//        addItemType(MultipleItem.SWITCH, R.layout.item_switch_setting);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.RIGHT_BUTTON:
                helper.setText(R.id.tv_setting_name, item.getName());
                helper.setText(R.id.tv_setting_value, item.getValue());
                break;
//            case MultipleItem.SWITCH:
//                helper.setText(R.id.tv_switch_setting_name, item.getName());
//                helper.setChecked(R.id.swh_status, item.isSwitch());
//                break;
        }
    }

}