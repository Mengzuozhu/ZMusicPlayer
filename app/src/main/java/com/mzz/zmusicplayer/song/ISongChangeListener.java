package com.mzz.zmusicplayer.song;

/**
 * @author : Mzz
 * date : 2019 2019/6/9 21:03
 * description :
 */
public interface ISongChangeListener {
    /**
     * Update play song background color.
     *
     * @param song the song
     */
    void updatePlaySongBackgroundColor(SongInfo song);
}
