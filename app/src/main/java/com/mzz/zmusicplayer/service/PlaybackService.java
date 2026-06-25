package com.mzz.zmusicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.MainActivity;
import com.mzz.zmusicplayer.common.NotificationHandler;
import com.mzz.zmusicplayer.manage.ListenerManager;
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
    private static final long UPDATE_POSITION_INTERVAL_MS = 1000L;
    private final Binder mBinder = new LocalBinder();
    private final Handler positionHandler = new Handler(Looper.getMainLooper());
    private final Runnable positionUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (stopping || mediaSession == null || !isPlaying()) {
                return;
            }
            updateMediaSessionState();
            positionHandler.postDelayed(this, UPDATE_POSITION_INTERVAL_MS);
        }
    };
    private BroadcastReceiver lockScreenReceiver;
    private NotificationHandler notificationHandler;
    private MediaSessionCompat mediaSession;
    private Player mPlayer;
    private boolean stopping;
    private boolean mediaSessionReady;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        initMediaSession();
        registerLockScreenReceiver();
        showNotification();
        if (mPlayer.isPlaying()) {
            ListenerManager.handlePlayStatusChanged(true);
        }
        positionHandler.post(() -> mediaSessionReady = true);
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
                stopAndExitApp();
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
        stopPositionUpdates();
        ListenerManager.handlePlayStatusChanged(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
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
        ListenerManager.handlePlayStatusChanged(isPlaying);
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
                    if (mediaSession != null && !mediaSession.isActive()) {
                        mediaSession.setActive(true);
                    }
                    updateMediaSessionState();
                    showNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(lockScreenReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(lockScreenReceiver, filter);
        }
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(this, "ZMusicPlayer");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        Intent sessionIntent = new Intent(this, MainActivity.class);
        PendingIntent sessionActivity = PendingIntent.getActivity(this, 0, sessionIntent,
                NotificationHandler.getPendingIntentFlags());
        mediaSession.setSessionActivity(sessionActivity);
        mediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (!mediaSessionReady) {
                    return;
                }
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
                stopAndExitApp();
            }

            @Override
            public void onSeekTo(long pos) {
                mPlayer.seekTo((int) pos);
                updateMediaSessionState();
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
                | PlaybackStateCompat.ACTION_STOP
                | PlaybackStateCompat.ACTION_SEEK_TO;

        boolean playing = isPlaying();
        int state = playing
                ? PlaybackStateCompat.STATE_PLAYING
                : PlaybackStateCompat.STATE_PAUSED;
        long position = mPlayer.getPlaybackPositionMs();
        float speed = playing ? 1.0f : 0.0f;

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(actions);
        if (playing) {
            stateBuilder.setState(state, position, speed, SystemClock.elapsedRealtime());
        } else {
            stateBuilder.setState(state, position, speed);
        }
        mediaSession.setPlaybackState(stateBuilder.build());

        SongInfo currentSong = mPlayer.getPlayingSong();
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        if (currentSong != null) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getName());
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist());
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, currentSong.getName());
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, currentSong.getArtist());
            if (currentSong.getDuration() > 0) {
                metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDuration());
            }
        }
        mediaSession.setMetadata(metadataBuilder.build());
    }

    /**
     * Show a notification while this service is running.
     * 播放/暂停时通知均可滑除，滑除后先暂停再退出应用。
     */
    private void showNotification() {
        if (stopping) {
            return;
        }
        if (notificationHandler == null) {
            notificationHandler = new NotificationHandler(this);
        }
        updateMediaSessionState();

        boolean playing = isPlaying();
        Notification notification = notificationHandler.buildMediaNotification(
                getPlayingSong(),
                playing,
                getPendingIntent(ACTION_PLAY_PRE),
                getPendingIntent(ACTION_PLAY_TOGGLE),
                getPendingIntent(ACTION_PLAY_NEXT),
                getPendingIntent(ACTION_STOP_SERVICE),
                mediaSession.getSessionToken());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        if (playing) {
            schedulePositionUpdates();
        } else {
            stopPositionUpdates();
        }
    }

    private void stopAndExitApp() {
        if (stopping) {
            return;
        }
        stopping = true;
        stopPositionUpdates();
        if (isPlaying()) {
            pause();
        }
        stopSelf();
        MusicApplication.exitApp();
    }

    private void schedulePositionUpdates() {
        stopPositionUpdates();
        if (isPlaying()) {
            positionHandler.postDelayed(positionUpdateRunnable, UPDATE_POSITION_INTERVAL_MS);
        }
    }

    private void stopPositionUpdates() {
        positionHandler.removeCallbacks(positionUpdateRunnable);
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
        Intent intent = new Intent(this, PlaybackService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, action.hashCode(), intent,
                NotificationHandler.getPendingIntentFlags());
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

}
