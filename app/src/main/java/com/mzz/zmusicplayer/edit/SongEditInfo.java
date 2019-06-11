package com.mzz.zmusicplayer.edit;

import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.play.SongListType;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/11 18:45
 * description :
 */
@AllArgsConstructor
public class SongEditInfo {
    @Getter
    SongInfo songInfo;
    @Getter
    SongListType songListType;
}
