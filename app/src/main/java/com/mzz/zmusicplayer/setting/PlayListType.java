package com.mzz.zmusicplayer.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 播放列表选择
 * author : Mzz
 * date : 2019 2019/6/7 18:24
 * description :
 */
@AllArgsConstructor
public enum PlayListType {

    LOCAL(0, "本地"),
    RECENT(1, "最近"),
    FAVORITE(2, "喜欢");

    @Getter
    private final int id;
    @Getter
    private final String desc;//中文描述

}
