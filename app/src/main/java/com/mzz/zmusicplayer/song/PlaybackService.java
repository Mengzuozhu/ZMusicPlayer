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
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.NotificationHandler;
import com.mzz.zmusicplayer.setting.PlayedMode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 4:27 PM
 * Desc: PlayService
 */
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
    private RemoteViews mContentViewSmall;
    private Player mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        registerLockScreenReceiver();
        showNotification();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterCallback(this);
        releasePlayer();
        if (lockScreenReceiver != null) {
            unregisterReceiver(lockScreenReceiver);
        }
        super.onDestroy();
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

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public SongInfo getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    private void unregisterCallback(PlayObserver callback) {
        mPlayer.unregisterCallback(callback);
    }

    private void releasePlayer() {
        mPlayer.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onSwitchFavorite(boolean isFavorite) {
        showNotification();
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
    public void onSwitchPlayMode(PlayedMode playedMode) {
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
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.content_notify);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        SparseArray <DrawableAndAction> idAndDrawables = new SparseArray <>();
        idAndDrawables.put(R.id.iv_notify_close,
                new DrawableAndAction(android.R.drawable.ic_menu_close_clear_cancel,
                        ACTION_STOP_SERVICE));
        idAndDrawables.put(R.id.iv_favorite, new DrawableAndAction(R.drawable.favorite_white,
                ACTION_SWITCH_FAVORITE));
        idAndDrawables.put(R.id.iv_play_pre, new DrawableAndAction(R.drawable.previous,
                ACTION_PLAY_PRE));
        idAndDrawables.put(R.id.iv_play_pause, new DrawableAndAction(R.drawable.play,
                ACTION_PLAY_TOGGLE));
        idAndDrawables.put(R.id.iv_play_next, new DrawableAndAction(R.drawable.next,
                ACTION_PLAY_NEXT));
        idAndDrawables.put(R.id.iv_play_mode, new DrawableAndAction(R.drawable.order,
                ACTION_PLAY_MODE));

        for (int i = 0; i < idAndDrawables.size(); i++) {
            int id = idAndDrawables.keyAt(i);
            DrawableAndAction drawableAndAction = idAndDrawables.valueAt(i);
            remoteView.setImageViewResource(id, drawableAndAction.drawable);
            remoteView.setOnClickPendingIntent(id, getPendingIntent(drawableAndAction.action));
        }
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        SongInfo currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.tv_song_name, String.format("%s-%s",
                    currentSong.getName(), currentSong.getArtist()));
            remoteView.setImageViewResource(R.id.iv_favorite, currentSong.getIsFavorite()
                    ? R.drawable.favorite : R.drawable.favorite_white);
        } else {
            String undefined = this.getString(R.string.undefined);
            remoteView.setTextViewText(R.id.tv_song_name, undefined);
        }
        remoteView.setImageViewResource(R.id.iv_play_pause, isPlaying()
                ? R.drawable.pause : R.drawable.play);
        PlayedMode playMode = mPlayer.getPlayList().getPlayMode();
        remoteView.setImageViewResource(R.id.iv_play_mode, playMode.getIcon());
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @AllArgsConstructor
    class DrawableAndAction {
        @Getter
        Integer drawable;
        @Getter
        String action;
    }

}
