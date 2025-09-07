package com.mzz.zmusicplayer.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.mzz.zmusicplayer.MainActivity;

/**
 * @author : Mzz
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
        // 将通知重要性提升为HIGH，确保通知直接显示而不是折叠到更多通知中
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        // 设置锁屏可见性
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
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
                .setContentIntent(getPendingIntent())
                // 设置锁屏可见性
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // 确保通知展开显示
                .setShowWhen(false);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Builder getNotification25Builder() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(getPendingIntent())
                // 设置锁屏可见性
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // 提高通知优先级，避免被折叠
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }

}