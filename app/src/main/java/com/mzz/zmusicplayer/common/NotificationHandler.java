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
        // 使用重要性，确保通知始终显示且不被折叠
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        
        // 设置锁屏可见性 - 完全可见
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        
        // 启用锁屏通知
        channel.enableLights(true);

        // 禁用振动，避免干扰
        channel.enableVibration(false);
        
        // 设置绕过免打扰模式
        channel.setBypassDnd(true);

        // 设置声音为null，避免重复播放
        channel.setSound(null, null);

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
                // 设置锁屏可见性 - 完全可见
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // 确保通知展开显示
                .setShowWhen(false)
                // 设置通知类别为媒体播放
                .setCategory(Notification.CATEGORY_TRANSPORT)
                // 设置最高优先级，避免被折叠
                .setPriority(Notification.PRIORITY_MAX);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Builder getNotification25Builder() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(getPendingIntent())
                // 设置锁屏可见性 - 完全可见
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // 设置最高优先级，避免被折叠
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // 设置通知类别为媒体播放
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT);
    }

}