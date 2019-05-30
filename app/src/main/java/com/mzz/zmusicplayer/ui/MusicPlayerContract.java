package com.mzz.zmusicplayer.ui;

import android.support.annotation.Nullable;

import com.mzz.zmusicplayer.song.PlaybackService;
import com.mzz.zmusicplayer.song.SongInfo;

interface MusicPlayerContract {

    interface View {

        void handleError(Throwable error);

        void onPlaybackServiceBound(PlaybackService service);

        void onPlaybackServiceUnbound();

        void onSongUpdated(@Nullable SongInfo song);
//
//        void updatePlayMode(PlayMode playMode);

        void updatePlayToggle(boolean play);
    }

    interface Presenter {

        void subscribe();

        void unsubscribe();

        void bindPlaybackService();

        void unbindPlaybackService();
    }
}
