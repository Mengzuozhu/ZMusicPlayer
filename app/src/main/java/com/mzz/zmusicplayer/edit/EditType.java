package com.mzz.zmusicplayer.edit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/9 15:30
 * description :
 */
@AllArgsConstructor
public enum EditType {
    PLAYLIST(1, "播放列表"),
    RECENT(2, "最近"),
    LOCAL(3, "本地"),
    FAVORITE(4, "喜欢");

    @Getter
    public final int code;
    @Getter
    private final String desc;//中文描述

}
