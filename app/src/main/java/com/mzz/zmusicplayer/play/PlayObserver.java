package com.mzz.zmusicplayer.play;

import android.support.annotation.Nullable;

import com.mzz.zmusicplayer.enums.PlayedMode;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * 播放器观察者
 *
 * @author : Mzz
 * date : 2019 2019/5/29 16:01
 * description :
 */
public interface PlayObserver {

    void onPlayStatusChanged(boolean isPlaying);

    void onSwitchPrevious(@Nullable SongInfo previous);

    void onSwitchFavorite(boolean isFavorite);

    void onSwitchNext(@Nullable SongInfo next);

    void onSwitchPlayMode(PlayedMode playedMode);

    void resetAllState();
}
