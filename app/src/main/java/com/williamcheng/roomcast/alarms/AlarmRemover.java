package com.williamcheng.roomcast.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.williamcheng.roomcast.classes.UpcomingNotification;

public class AlarmRemover {
    private final Context appContext;
    private final AlarmManager alarmManager;

    public AlarmRemover(Context context) {
        this.appContext = context.getApplicationContext();
        alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void remove(UpcomingNotification upcomingNotification) {
        Intent intent = new Intent(appContext, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, upcomingNotification.getId(), intent, 0);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
