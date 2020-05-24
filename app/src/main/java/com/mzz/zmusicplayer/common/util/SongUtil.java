package com.mzz.zmusicplayer.common.util;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * TODO
 *
 * @author zuozhu.meng
 * @date 2020/5/24
 **/
public class SongUtil {

    public static String getUpperSpell(String name) {
        String pinyin = Pinyin.toPinyin(name, "");
        return pinyin == null ? "" : pinyin.toUpperCase();
    }
}
