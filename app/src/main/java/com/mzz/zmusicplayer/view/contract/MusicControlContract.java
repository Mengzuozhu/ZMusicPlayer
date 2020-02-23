package com.mzz.zmusicplayer.view.contract;

import android.support.annotation.Nullable;

import com.mzz.zmusicplayer.song.SongInfo;

public interface MusicControlContract {

    interface View {

        void handleError(Throwable error);

        void onSongUpdated(@Nullable SongInfo song);

        void updatePlayToggle(boolean play);
    }

    interface Presenter {

        void subscribe();

        void unsubscribe();

        void bindPlaybackService();

        void unbindPlaybackService();
    }
}
