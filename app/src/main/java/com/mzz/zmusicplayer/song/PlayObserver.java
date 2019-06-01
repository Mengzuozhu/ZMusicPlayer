package com.mzz.zmusicplayer.song;

import android.support.annotation.Nullable;

/**
 * 播放器观察者
 * author : Mzz
 * date : 2019 2019/5/29 16:01
 * description :
 */
public interface PlayObserver {

    void onPlayStatusChanged(boolean isPlaying);

    void onSwitchPrevious(@Nullable SongInfo previous);

    void onSwitchNext(@Nullable SongInfo next);

    void resetAllState();
}
