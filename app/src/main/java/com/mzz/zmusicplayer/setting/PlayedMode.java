package com.mzz.zmusicplayer.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/5/6 10:52
 * description :
 */
@AllArgsConstructor
public enum PlayedMode {
    RANDOM(0, "随机播放"),
    SINGLE(1, "单曲循环"),
    ORDER(2, "顺序播放");

    @Getter
    private final int id;
    @Getter
    private final String desc;//中文描述

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

}
