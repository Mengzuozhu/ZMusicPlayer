package com.mzz.zmusicplayer.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/5/11 16:47
 * description :
 */
@AllArgsConstructor
public class MultipleItem implements MultiItemEntity {
    public static final int RIGHT_BUTTON = 1;
    public static final int SWITCH = 2;
    @Getter
    private String value;
    private int itemType;
    @Getter
    private String name;
    @Getter
    private boolean isSwitch = false;

    public MultipleItem(int itemType, String name, String value) {
        this.itemType = itemType;
        this.name = name;
        this.value = value;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}
