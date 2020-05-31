package com.mzz.zmusicplayer.common.util;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * TODO
 *
 * @author zuozhu.meng
 * @date 2020/5/24
 **/
public class SongUtil {

    /**
     * 获取大写拼音
     *
     * @param name the name
     * @return the upper spell
     */
    public static String getUpperSpell(String name) {
        String pinyin = Pinyin.toPinyin(name, "");
        return pinyin == null ? "" : pinyin.toUpperCase();
    }

    public static String joinSongShowedName(SongInfo songInfo) {
        return String.format("%s-%s", songInfo.getSongName(), songInfo.getArtist());
    }
}
