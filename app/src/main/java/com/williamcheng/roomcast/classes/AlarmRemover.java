package com.williamcheng.roomcast.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.williamcheng.roomcast.NotificationBroadcastReceiver;

public class AlarmRemover {
    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmRemover(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void remove(UpcomingNotification upcomingNotification) {
        Intent currIntent = new Intent(context.getApplicationContext(), NotificationBroadcastReceiver.class);
        PendingIntent currPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), upcomingNotification.getId(), currIntent, 0);
        alarmManager.cancel(currPendingIntent);
    }

}
