package com.williamcheng.roomcast.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.UpcomingNotification;

public class AlarmBuilder {
    private final Context appContext;
    private final AlarmManager alarmManager;

    public AlarmBuilder(Context context) {
        this.appContext = context.getApplicationContext();
        alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void build(UpcomingNotification upcomingNotification) {
        Intent intent = new Intent(appContext, NotificationBroadcastReceiver.class);
        Message message = upcomingNotification.getMessage();
        long interval = message.getInterval();
        int id = upcomingNotification.getId();
        long triggerTime = upcomingNotification.getTriggerTime();

        intent.putExtra("Title", message.getTitle());
        intent.putExtra("Body", message.getBody());
        intent.putExtra("Interval", interval);
        intent.putExtra("Id", id);
        intent.putExtra("Time", upcomingNotification.getTriggerTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, id, intent, 0);

        if(interval == Interval.ONCE || interval == Interval.MONTHLY_START || interval == Interval.MONTHLY_END) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
        }

    }
}
