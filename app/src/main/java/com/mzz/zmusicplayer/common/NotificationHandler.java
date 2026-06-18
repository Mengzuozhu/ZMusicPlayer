package com.mzz.zmusicplayer.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaSessionCompat;

import com.mzz.zmusicplayer.MainActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.song.SongInfo;

/**
 * 媒体播放通知构建器，使用 MediaStyle 以兼容小米 HyperOS 锁屏与通知栏展示。
 *
 * @author : Mzz
 * date : 2019 2019/5/30 21:57
 */
public class NotificationHandler extends ContextWrapper {

    public static final String CHANNEL_ID = "channel_media_playback";
    private static final String LEGACY_CHANNEL_ID = "channel_1";
    private static final String CHANNEL_NAME = "音乐播放";
    private NotificationManager manager;

    public NotificationHandler(Context base) {
        super(base);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager notificationManager = getManager();
        notificationManager.deleteNotificationChannel(LEGACY_CHANNEL_ID);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("音乐播放控制");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setShowBadge(false);
        channel.enableVibration(false);
        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
    }

    public Notification buildMediaNotification(SongInfo song, boolean isPlaying,
                                               PendingIntent previousIntent,
                                               PendingIntent playPauseIntent,
                                               PendingIntent nextIntent,
                                               PendingIntent stopIntent,
                                               MediaSessionCompat.Token sessionToken) {
        createNotificationChannel();

        String title = getString(R.string.undefined);
        String artist = "";
        if (song != null) {
            title = song.getName();
            artist = song.getArtist();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.music)
                .setContentTitle(title)
                .setContentText(artist)
                .setContentIntent(getContentPendingIntent())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setShowWhen(false)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.previous, getString(R.string.notify_action_previous), previousIntent)
                .addAction(isPlaying ? R.drawable.pause : R.drawable.play,
                        getString(R.string.notify_action_play_pause), playPauseIntent)
                .addAction(R.drawable.next, getString(R.string.notify_action_next), nextIntent)
                .addAction(R.drawable.close, getString(R.string.notify_action_stop), stopIntent)
                .setStyle(new MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(sessionToken)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopIntent));

        return builder.build();
    }

    private PendingIntent getContentPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, getPendingIntentFlags());
    }

    public static int getPendingIntentFlags() {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 31) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return flags;
    }


    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

}
