package com.mzz.zmusicplayer.view.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.mzz.zmusicplayer.service.PlaybackService;
import com.mzz.zmusicplayer.view.contract.MusicControlContract;

/**
 * @author Mengzz
 */
public class MusicControlPresenter implements MusicControlContract.Presenter {

    private Context mContext;
    private MusicControlContract.View mView;
    private PlaybackService mPlaybackService;
    private boolean mIsServiceBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mPlaybackService = null;
        }
    };

    public MusicControlPresenter(Context context, MusicControlContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void subscribe() {
        bindPlaybackService();
        if (mPlaybackService != null && mPlaybackService.isPlaying()) {
            mView.onSongUpdated(mPlaybackService.getPlayingSong());
        }
    }

    @Override
    public void unsubscribe() {
        unbindPlaybackService();
        mContext = null;
        mView = null;
    }

    @Override
    public void bindPlaybackService() {
        mContext.bindService(new Intent(mContext, PlaybackService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    @Override
    public void unbindPlaybackService() {
        if (mIsServiceBound) {
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            mIsServiceBound = false;
        }
    }
}
