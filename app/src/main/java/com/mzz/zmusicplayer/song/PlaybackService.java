package com.mzz.zmusicplayer.song;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mzz.zmusicplayer.MainActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.setting.PlayedMode;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 4:27 PM
 * Desc: PlayService
 */
public class PlaybackService extends Service implements IPlayer, PlayObserver {

    private static final String ACTION_PLAY_TOGGLE = "io.github.ryanhoo.music.ACTION.PLAY_TOGGLE";
    private static final String ACTION_PLAY_LAST = "io.github.ryanhoo.music.ACTION.PLAY_LAST";
    private static final String ACTION_PLAY_NEXT = "io.github.ryanhoo.music.ACTION.PLAY_NEXT";
    private static final String ACTION_STOP_SERVICE = "io.github.ryanhoo.music.ACTION.STOP_SERVICE";

    private static final int NOTIFICATION_ID = 1;
    private final Binder mBinder = new LocalBinder();
    private RemoteViews mContentViewBig, mContentViewSmall;
    private Player mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY_TOGGLE.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (ACTION_PLAY_NEXT.equals(action)) {
                playNext();
            } else if (ACTION_PLAY_LAST.equals(action)) {
                playPrevious();
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                if (isPlaying()) {
                    pause();
                }
                stopForeground(true);
                unregisterCallback(this);
            }
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
    public SongInfo getCurrentSong() {
        return mPlayer.getCurrentSong();
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

    // Playback Callbacks

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        showNotification();
    }

//    @Override
//    public void onComplete(@Nullable SongInfo next) {
//        showNotification();
//    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();

    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
//                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }

    // Notification

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(),
                    R.layout.fragment_control);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

//    private RemoteViews getBigContentView() {
//        if (mContentViewBig == null) {
//            mContentViewBig = new RemoteViews(getPackageName(), R.layout
//            .remote_view_music_player);
//            setUpRemoteView(mContentViewBig);
//        }
//        updateRemoteViews(mContentViewBig);
//        return mContentViewBig;
//    }

    private void setUpRemoteView(RemoteViews remoteView) {
//        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
        remoteView.setImageViewResource(R.id.iv_play_pre,
                R.drawable.previous);
        remoteView.setImageViewResource(R.id.iv_play_pause,
                R.drawable.play);
        remoteView.setImageViewResource(R.id.iv_play_next,
                R.drawable.next);

//        remoteView.setOnClickPendingIntent(R.id.button_close,
//                getPendingIntent(ACTION_STOP_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.iv_play_pre,
                getPendingIntent(ACTION_PLAY_LAST));
        remoteView.setOnClickPendingIntent(R.id.iv_play_next,
                getPendingIntent(ACTION_PLAY_NEXT));
        remoteView.setOnClickPendingIntent(R.id.iv_play_pause,
                getPendingIntent(ACTION_PLAY_TOGGLE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        SongInfo currentSong = mPlayer.getCurrentSong();
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.tv_song_name, currentSong.getName());
            remoteView.setTextViewText(R.id.tv_artist, currentSong.getArtist());
        }
        remoteView.setImageViewResource(R.id.iv_play_pause, isPlaying()
                ? R.drawable.pause : R.drawable.play);
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

    // PendingIntent

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }
}
