package com.mzz.zmusicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.common.NotificationHandler;
import com.mzz.zmusicplayer.enums.PlayedMode;
import com.mzz.zmusicplayer.play.PlayObserver;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * 后台服务
 *
 * @author zuozhu.meng
 **/
public class PlaybackService extends Service implements PlayObserver {

    private static final String ACTION_PLAY_TOGGLE = "com.mzz.zmusicplayer.ACTION.PLAY_TOGGLE";
    private static final String ACTION_SWITCH_FAVORITE = "com.mzz.zmusicplayer.ACTION.FAVORITE";
    private static final String ACTION_PLAY_PRE = "com.mzz.zmusicplayer.ACTION.PLAY_PRE";
    private static final String ACTION_PLAY_NEXT = "com.mzz.zmusicplayer.ACTION.PLAY_NEXT";
    private static final String ACTION_STOP_SERVICE = "com.mzz.zmusicplayer.ACTION.STOP_SERVICE";
    private static final String ACTION_PLAY_MODE = "com.mzz.zmusicplayer.ACTION.PLAY_MODE";
    private static final int NOTIFICATION_ID = 1;
    private final Binder mBinder = new LocalBinder();
    private BroadcastReceiver lockScreenReceiver;
    private NotificationHandler notificationHandler;
    private MediaSessionCompat mediaSession;
    private Player mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        initMediaSession();
        registerLockScreenReceiver();
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || mPlayer == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if (action == null) {
            return START_STICKY;
        }
        switch (action) {
            case ACTION_PLAY_PRE:
                playPrevious();
                break;
            case ACTION_SWITCH_FAVORITE:
                switchFavorite();
                break;
            case ACTION_PLAY_TOGGLE:
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
                break;
            case ACTION_PLAY_NEXT:
                playNext();
                break;
            case ACTION_STOP_SERVICE:
                if (isPlaying()) {
                    pause();
                }
                stopSelf();
                MusicApplication.exitApp();
                break;
            case ACTION_PLAY_MODE:
                changePlayMode();
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterCallback(this);
        if (lockScreenReceiver != null) {
            unregisterReceiver(lockScreenReceiver);
        }
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
            mediaSession = null;
        }
        mPlayer.releasePlayer();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public SongInfo getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();
    }

    @Override
    public void onSwitchPrevious(@Nullable SongInfo last) {
        showNotification();
    }

    @Override
    public void onSwitchFavorite(boolean isFavorite) {
        showNotification();
    }

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        showNotification();
    }

    @Override
    public void onSwitchPlayMode(PlayedMode playedMode) {
        showNotification();
    }

    @Override
    public void onSongNameChanged(SongInfo songInfo) {
        showNotification();
    }

    @Override
    public void resetAllState() {
        showNotification();
    }

    private void registerLockScreenReceiver() {
        lockScreenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    showNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, filter);
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(this, "ZMusicPlayer");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNext();
            }

            @Override
            public void onSkipToPrevious() {
                playPrevious();
            }

            @Override
            public void onStop() {
                if (isPlaying()) {
                    pause();
                }
                stopSelf();
                MusicApplication.exitApp();
            }
        });
        mediaSession.setActive(true);
    }

    private void updateMediaSessionState() {
        if (mediaSession == null) {
            return;
        }

        long actions = PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_STOP;

        int state = isPlaying()
                ? PlaybackStateCompat.STATE_PLAYING
                : PlaybackStateCompat.STATE_PAUSED;

        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(actions)
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                .build());

        SongInfo currentSong = mPlayer.getPlayingSong();
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        if (currentSong != null) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getName());
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist());
            if (currentSong.getDuration() > 0) {
                metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDuration());
            }
        }
        mediaSession.setMetadata(metadataBuilder.build());
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        if (notificationHandler == null) {
            notificationHandler = new NotificationHandler(this);
        }
        updateMediaSessionState();

        Notification notification = notificationHandler.buildMediaNotification(
                getPlayingSong(),
                isPlaying(),
                getPendingIntent(ACTION_PLAY_PRE),
                getPendingIntent(ACTION_PLAY_TOGGLE),
                getPendingIntent(ACTION_PLAY_NEXT),
                getPendingIntent(ACTION_STOP_SERVICE),
                mediaSession.getSessionToken());
        startForeground(NOTIFICATION_ID, notification);
    }

    private void play() {
        mPlayer.play();
    }

    private void switchFavorite() {
        mPlayer.switchFavorite();
    }

    private void playPrevious() {
        mPlayer.playPrevious();
    }

    private void playNext() {
        mPlayer.playNext();
    }

    private void pause() {
        mPlayer.pause();
    }

    private void changePlayMode() {
        mPlayer.changePlayMode();
    }

    private void unregisterCallback(PlayObserver callback) {
        mPlayer.unregisterCallback(callback);
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, action.hashCode(), new Intent(action),
                NotificationHandler.getPendingIntentFlags());
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

}
