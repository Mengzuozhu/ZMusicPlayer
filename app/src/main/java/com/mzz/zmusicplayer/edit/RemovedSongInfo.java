package com.mzz.zmusicplayer.edit;

import com.mzz.zmusicplayer.play.SongListType;
import com.mzz.zmusicplayer.song.SongInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 被移除的歌曲信息
 * author : Mzz
 * date : 2019 2019/6/11 18:45
 * description :
 */
@AllArgsConstructor
public class RemovedSongInfo {
    @Getter
    SongInfo songInfo;
    @Getter
    SongListType songListType;
}
