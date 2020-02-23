package com.mzz.zmusicplayer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Mzz
 * date : 2019 2019/5/31 19:32
 * description :
 */
@AllArgsConstructor
public enum SongOrderMode {

    ORDER_ASCEND_BY_ADD_TIME(0, "按添加时间排序"),
    ORDER_ASCEND_BY_NAME(1, "按歌名升序"),
    ORDER_DESCEND_BY_NAME(2, "按歌名降序"),
    ORDER_DESCEND_BY_PLAY_COUNT(3, "按播放量排序");

    @Getter
    private final int id;
    /**
     * 中文描述
     */
    @Getter
    private final String desc;

}
