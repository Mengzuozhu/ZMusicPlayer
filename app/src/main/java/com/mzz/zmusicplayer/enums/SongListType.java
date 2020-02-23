package com.mzz.zmusicplayer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 歌曲列表类型
 *
 * @author : Mzz
 * date : 2019 2019/6/9 15:30
 * description :
 */
@AllArgsConstructor
public enum SongListType {
    /**
     * Playlist song list type.
     */
    PLAYLIST(1, "播放"),
    /**
     * Recent song list type.
     */
    RECENT(2, "最近"),
    /**
     * Local song list type.
     */
    LOCAL(3, "本地"),
    /**
     * Favorite song list type.
     */
    FAVORITE(4, "喜欢");

    @Getter
    public final int code;
    /**
     * 中文描述
     */
    @Getter
    private final String desc;

}
