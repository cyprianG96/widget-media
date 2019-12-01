package com.mobica.widgets.navigation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mobica.widgets.R;
import com.mobica.widgets.StartActivity;

import java.util.Objects;

public class NavClientService extends Service {
    static class ACTION {
        static String START_FOREGROUND = "com.mobica.widgets.navigation.action.startforeground";
        static String STOP_FOREGROUND = "com.mobica.widgets.navigation.action.stopforeground";
    }
    public static int NOTIFICATION_ID = 101;
    public static final String CHANNEL_ID = "NavigationWidgetServiceChannel";

    private NavAppAPI navAppAPI;

    public NavClientService() {
        Log.i("ZXC", "Constructor NavClientService -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Navigation Widget Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.equals(intent.getAction(), ACTION.START_FOREGROUND)) {
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, StartActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Navigation")
                    .setContentText("Foreground Service for widget")
                    .setSmallIcon(R.mipmap.ic_launcher_mobica)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(NOTIFICATION_ID, notification);
            navAppAPI = new NavAppAPI(this);
        } else if (Objects.equals(intent.getAction(), ACTION.STOP_FOREGROUND)) {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navAppAPI.close();
    }
}
