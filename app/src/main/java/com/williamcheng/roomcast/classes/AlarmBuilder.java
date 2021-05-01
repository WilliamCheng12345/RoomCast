package com.williamcheng.roomcast.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.williamcheng.roomcast.NotificationBroadcastReceiver;

public class AlarmBuilder {
    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmBuilder(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void build(long triggerTime, SavedNotification savedNotification) {
        Intent currIntent = new Intent(context.getApplicationContext(), NotificationBroadcastReceiver.class);
        Message message = savedNotification.getMessage();
        long interval = message.getInterval();
        int id = savedNotification.getId();

        currIntent.putExtra("Title", message.getTitle());
        currIntent.putExtra("Body", message.getBody());
        currIntent.putExtra("Interval", interval);
        currIntent.putExtra("Id", id);
        currIntent.putExtra("Time", savedNotification.getTimeStamp());

        PendingIntent currPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), savedNotification.getId(), currIntent, 0);

        if(interval == Interval.ONCE || interval == Interval.MONTHLY_START || interval == Interval.MONTHLY_END) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, currPendingIntent);
        }
        else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, currPendingIntent);
        }

    }
}