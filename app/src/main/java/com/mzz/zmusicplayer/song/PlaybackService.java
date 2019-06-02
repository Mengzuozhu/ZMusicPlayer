package com.mzz.zmusicplayer.song;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.NotificationHandler;
import com.mzz.zmusicplayer.setting.PlayedMode;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 4:27 PM
 * Desc: PlayService
 */
public class PlaybackService extends Service implements IPlayer, PlayObserver {

    private static final String ACTION_PLAY_TOGGLE = "com.mzz.zmusicplayer.ACTION.PLAY_TOGGLE";
    private static final String ACTION_PLAY_LAST = "com.mzz.zmusicplayer.ACTION.PLAY_LAST";
    private static final String ACTION_PLAY_NEXT = "com.mzz.zmusicplayer.ACTION.PLAY_NEXT";
    private static final String ACTION_STOP_SERVICE = "com.mzz.zmusicplayer.ACTION.STOP_SERVICE";
    private static final int NOTIFICATION_ID = 1;
    private final Binder mBinder = new LocalBinder();
    private BroadcastReceiver lockScreenReceiver;
    private NotificationHandler notificationHandler;
    private RemoteViews mContentViewSmall;
    private Player mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        registerLockScreenReceiver();
    }

    private void registerLockScreenReceiver() {
        lockScreenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    //锁屏后，显示播放工具栏
                    showNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if (action == null) {
            return START_STICKY;
        }
        switch (action) {
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
            case ACTION_PLAY_LAST:
                playPrevious();
                break;
            case ACTION_STOP_SERVICE:
                if (isPlaying()) {
                    pause();
                }
                stopForeground(true);
                unregisterCallback(this);
                break;
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean stopService(Intent name) {
        stopForeground(true);
        unregisterCallback(this);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        if (lockScreenReceiver != null) {
            unregisterReceiver(lockScreenReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void setPlayList(PlayList list) {
        mPlayer.setPlayList(list);
    }

    @Override
    public boolean play() {
        return mPlayer.play();
    }

    @Override
    public boolean play(PlayList list) {
        return mPlayer.play(list);
    }

    @Override
    public boolean play(int playingIndex) {
        return mPlayer.play(playingIndex);
    }

    @Override
    public boolean playPrevious() {
        return mPlayer.playPrevious();
    }

    @Override
    public boolean playNext() {
        return mPlayer.playNext();
    }

    @Override
    public boolean pause() {
        return mPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public SongInfo getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public boolean seekTo(int progress) {
        return mPlayer.seekTo(progress);
    }

    @Override
    public int getCurrentSongDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public int getPlayingIndex() {
        return 0;
    }

    @Override
    public void setPlayMode(PlayedMode playMode) {
        mPlayer.setPlayMode(playMode);
    }

    @Override
    public void registerCallback(PlayObserver callback) {
        mPlayer.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(PlayObserver callback) {
        mPlayer.unregisterCallback(callback);
    }

    @Override
    public void clearCallbacks() {
        mPlayer.clearCallbacks();
    }

    @Override
    public void releasePlayer() {
        mPlayer.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onSwitchPrevious(@Nullable SongInfo last) {
        showNotification();
    }

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        showNotification();
    }

    @Override
    public void resetAllState() {
        showNotification();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        Notification notification;
        if (notificationHandler == null) {
            notificationHandler = new NotificationHandler(this);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder builder = notificationHandler.getChannelNotificationBuilder();
            //不使用自定义大视图，否则会折叠通知栏
            builder.setCustomContentView(getSmallContentView())
                    .setSmallIcon(R.drawable.music)
                    .setOngoing(true);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = notificationHandler.getNotification25Builder();
            builder.setCustomContentView(getSmallContentView())
                    .setSmallIcon(R.drawable.music)
                    .setOngoing(true);
            notification = builder.build();
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.notify_control);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.iv_notify_close,
                android.R.drawable.ic_menu_close_clear_cancel);
        remoteView.setImageViewResource(R.id.iv_notify_play_pre, R.drawable.previous);
        remoteView.setImageViewResource(R.id.iv_notify_play_pause, R.drawable.play);
        remoteView.setImageViewResource(R.id.iv_notify_play_next, R.drawable.next);

//        remoteView.setOnClickPendingIntent(R.id.iv_notify_close,
//                getPendingIntent(ACTION_STOP_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.iv_notify_play_pre,
                getPendingIntent(ACTION_PLAY_LAST));
        remoteView.setOnClickPendingIntent(R.id.iv_notify_play_next,
                getPendingIntent(ACTION_PLAY_NEXT));
        remoteView.setOnClickPendingIntent(R.id.iv_notify_play_pause,
                getPendingIntent(ACTION_PLAY_TOGGLE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        SongInfo currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.tv_notify_song_name, currentSong.getName());
            remoteView.setTextViewText(R.id.tv_notify_artist, currentSong.getArtist());
        } else {
            String undefined = this.getString(R.string.undefined);
            remoteView.setTextViewText(R.id.tv_notify_song_name, undefined);
            remoteView.setTextViewText(R.id.tv_notify_artist, undefined);
        }
        remoteView.setImageViewResource(R.id.iv_notify_play_pause, isPlaying()
                ? R.drawable.pause : R.drawable.play);
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }
}
