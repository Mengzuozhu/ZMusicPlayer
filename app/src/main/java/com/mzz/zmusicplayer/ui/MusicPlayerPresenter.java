package com.mzz.zmusicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.mzz.zmusicplayer.song.PlaybackService;

public class MusicPlayerPresenter implements MusicPlayerContract.Presenter {

    private Context mContext;
    private MusicPlayerContract.View mView;

    private PlaybackService mPlaybackService;
    private boolean mIsServiceBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
            mView.onPlaybackServiceBound(mPlaybackService);
            mView.onSongUpdated(mPlaybackService.getPlayingSong());
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mPlaybackService = null;
            mView.onPlaybackServiceUnbound();
        }
    };

    public MusicPlayerPresenter(Context context, MusicPlayerContract.View view) {
        mContext = context;
        mView = view;
//        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        bindPlaybackService();

        if (mPlaybackService != null && mPlaybackService.isPlaying()) {
            mView.onSongUpdated(mPlaybackService.getPlayingSong());
        } else {
            // - load last play list/folder/song
        }
    }

    @Override
    public void unsubscribe() {
        unbindPlaybackService();
        // Release context reference
        mContext = null;
        mView = null;
    }

    @Override
    public void bindPlaybackService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
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
