package com.mzz.zmusicplayer.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/5/31 19:32
 * description :
 */
@AllArgsConstructor
public enum SongOrderMode {

    ORDER_ASCEND_BY_ADD_TIME(0, "按添加时间排序"),
    ORDER_ASCEND_BY_NAME(1, "按歌名升序"),
    ORDER_DESCEND_BY_NAME(2, "按歌名降序");

    @Getter
    private final int id;
    @Getter
    private final String desc;//中文描述

    public static SongOrderMode getDefault() {
        return ORDER_ASCEND_BY_ADD_TIME;
    }
}
