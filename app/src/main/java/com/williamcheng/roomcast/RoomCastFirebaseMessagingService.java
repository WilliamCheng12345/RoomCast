package com.williamcheng.roomcast;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class RoomCastFirebaseMessagingService  extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        int interval = Integer.parseInt(remoteMessage.getData().get("interval"));

        Intent currIntent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);

        currIntent.putExtra("Title", title);
        currIntent.putExtra("Body", body);

        System.out.println("Message received");

        long currTime = System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int id = (int) currTime;
        PendingIntent currPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, currIntent,0);

        switch(interval) {
            case Interval.ONCE:
                createRepeatingAlarm(alarmManager, currTime, 10*1000, currPendingIntent);
                break;
            case Interval.HALF_HOUR:
                createRepeatingAlarm(alarmManager, currTime, AlarmManager.INTERVAL_HALF_HOUR, currPendingIntent);
                break;
            case Interval.HOURLY:
                createRepeatingAlarm(alarmManager, currTime, AlarmManager.INTERVAL_HOUR, currPendingIntent);
                break;
            case  Interval.DAILY:
                createRepeatingAlarm(alarmManager, currTime, AlarmManager.INTERVAL_DAY, currPendingIntent);
                break;
            case Interval.WEEKLY:
                createRepeatingAlarm(alarmManager, currTime, AlarmManager.INTERVAL_DAY*7, currPendingIntent);
                break;
        }
    }

    private void createRepeatingAlarm(AlarmManager alarmManager, long currTime, long interval, PendingIntent currPendingIntent) {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currTime, interval, currPendingIntent);
    }
}
