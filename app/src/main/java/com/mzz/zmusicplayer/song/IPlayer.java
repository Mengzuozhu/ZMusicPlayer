package com.mzz.zmusicplayer.song;

/**
 * author : Mzz
 * date : 2019 2019/5/29 16:08
 * description :
 */
public interface IPlayer {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean pause();

    boolean playPrevious();

    boolean playNext();

    boolean isPlaying();

    SongInfo getCurrentSong();

    int getCurrentSongDuration();

    int getCurrentPosition();

    boolean seekTo(int progress);

    void releasePlayer();

    void registerCallback(PlayObserver PlayObserver);

    void unregisterCallback(PlayObserver PlayObserver);

    void clearCallbacks();
}
