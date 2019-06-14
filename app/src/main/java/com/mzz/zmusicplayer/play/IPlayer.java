package com.mzz.zmusicplayer.play;

import com.mzz.zmusicplayer.song.SongInfo;

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

    void playPrevious();

    void pause();

    void playNext();

    void seekTo(int progress);

    boolean isPlaying();

    int getCurrentPosition();

    SongInfo getPlayingSong();

    void changePlayMode();

    void releasePlayer();

    void registerCallback(PlayObserver PlayObserver);

    void unregisterCallback(PlayObserver PlayObserver);

    void clearCallbacks();
}
