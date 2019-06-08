package com.mzz.zmusicplayer.song;

/**
 * author : Mzz
 * date : 2019 2019/5/29 16:08
 * description :
 */
public interface IPlayer {

    PlayList getPlayList();

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(int playingIndex);

    boolean play(SongInfo songInfo);

    void switchFavorite();

    boolean playPrevious();

    boolean pause();

    boolean playNext();

    boolean isPlaying();

    boolean seekTo(int progress);

    void continuePlay();

    int getCurrentPosition();

    SongInfo getPlayingSong();

    void changePlayMode();

    void releasePlayer();

    void registerCallback(PlayObserver PlayObserver);

    void unregisterCallback(PlayObserver PlayObserver);

    void clearCallbacks();
}
