package com.williamcheng.roomcast;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.williamcheng.roomcast.alarms.AlarmBuilder;
import com.williamcheng.roomcast.alarms.AlarmRemover;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.alarms.NotificationBroadcastReceiver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AlarmTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private Intent intent;

    @Test
    public void testAlarm() {
        intent = new Intent(context, NotificationBroadcastReceiver.class);
        Message message1 = new Message("Title1", "Body1", Interval.WEEKLY);
        UpcomingNotification upcomingNotification1 = new UpcomingNotification(message1,1, System.currentTimeMillis());
        Message message2 = new Message("Title2", "Body2", Interval.ONCE);
        UpcomingNotification upcomingNotification2 = new UpcomingNotification(message2,2, System.currentTimeMillis());

        buildAlarm(upcomingNotification1);
        buildAlarm(upcomingNotification2);
        removeAlarm(upcomingNotification1);
        removeAlarm(upcomingNotification2);
    }

    private void buildAlarm(UpcomingNotification upcomingNotification) {
        AlarmBuilder alarmBuilder = new AlarmBuilder(context);

        alarmBuilder.build(upcomingNotification);

        boolean isAlarmCreated = (PendingIntent.getBroadcast(context, 1,  intent, PendingIntent.FLAG_NO_CREATE) != null);

        Assert.assertTrue(isAlarmCreated);
    }

    private void removeAlarm(UpcomingNotification upcomingNotification) {
        AlarmRemover alarmRemover = new AlarmRemover(context);

        alarmRemover.remove(upcomingNotification);

        boolean isAlarmRemoved = (PendingIntent.getBroadcast(context, 1,  intent, PendingIntent.FLAG_NO_CREATE) == null);

        Assert.assertTrue(isAlarmRemoved);

    }
}
