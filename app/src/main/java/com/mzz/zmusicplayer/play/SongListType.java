package com.mzz.zmusicplayer.play;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 歌曲列表类型
 * author : Mzz
 * date : 2019 2019/6/9 15:30
 * description :
 */
@AllArgsConstructor
public enum SongListType {
    PLAYLIST(1, "播放"),
    RECENT(2, "最近"),
    LOCAL(3, "本地"),
    FAVORITE(4, "喜欢");

    @Getter
    public final int code;
    @Getter
    private final String desc;//中文描述

}
