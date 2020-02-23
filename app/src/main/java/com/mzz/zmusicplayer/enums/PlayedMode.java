package com.mzz.zmusicplayer.enums;

import com.mzz.zmusicplayer.R;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Mzz
 * date : 2019 2019/5/6 10:52
 * description :
 */
@AllArgsConstructor
public enum PlayedMode {
    ORDER(0, "顺序播放"),
    SINGLE(1, "单曲循环"),
    RANDOM(2, "随机播放");

    @Getter
    private final int id;
    /**
     * 中文描述
     */
    @Getter
    private final String desc;

    /**
     * Gets next mode.
     *
     * @return the next mode
     */
    public PlayedMode getNextMode() {
        int nextId = id;
        nextId++;
        int length = values().length;
        if (nextId >= length) {
            nextId = nextId % length;
        }
        return values()[nextId];
    }

    public int getIcon() {
        switch (id) {
            case 0:
                return R.drawable.order;
            case 1:
                return R.drawable.single;
            case 2:
                return R.drawable.random;
            default:
                return R.drawable.order;
        }
    }
}
