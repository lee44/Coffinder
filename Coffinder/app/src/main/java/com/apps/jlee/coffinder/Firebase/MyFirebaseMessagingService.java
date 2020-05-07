package com.apps.jlee.coffinder.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.apps.jlee.coffinder.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Random;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
//        Log.v("Lakers",remoteMessage.getNotification().getBody());
        shownotification(remoteMessage.getNotification());
    }

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);

        getSharedPreferences("Device Token", MODE_PRIVATE).edit().putString("Token", s).apply();
//        Log.v("NEW_TOKEN",s);
    }

    public static String getToken(Context context)
    {
        return context.getSharedPreferences("Device Token", MODE_PRIVATE).getString("Token", "None");
    }

    public void shownotification(RemoteMessage.Notification message)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.apps.jlee.boginder"; //your app package name

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Test Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(message.getTitle())
                .setContentText(message.getBody())
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
    }
}
