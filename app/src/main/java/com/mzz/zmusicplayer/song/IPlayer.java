package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.setting.PlayedMode;

/**
 * author : Mzz
 * date : 2019 2019/5/29 16:08
 * description :
 */
public interface IPlayer {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(int playingIndex);

    boolean pause();

    boolean playPrevious();

    boolean playNext();

    boolean isPlaying();

    boolean seekTo(int progress);

    int getCurrentPosition();

    SongInfo getPlayingSong();

    void setPlayMode(PlayedMode playMode);

    void releasePlayer();

    void registerCallback(PlayObserver PlayObserver);

    void unregisterCallback(PlayObserver PlayObserver);

    void clearCallbacks();
}
