package com.mzz.zmusicplayer.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.mzz.zmusicplayer.MainActivity;

/**
 * author : Mzz
 * date : 2019 2019/5/30 21:57
 * description :
 */
public class NotificationHandler extends ContextWrapper {
    public static final String CHANNEL_ID = "channel_1";
    public static final String CHANNEL_NAME = "channel_name_1";
    private NotificationManager manager;

    public NotificationHandler(Context base) {
        super(base);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotificationBuilder() {
        createNotificationChannel();
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), 0);
    }

    public NotificationCompat.Builder getNotification25Builder() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(getPendingIntent());
    }

}
