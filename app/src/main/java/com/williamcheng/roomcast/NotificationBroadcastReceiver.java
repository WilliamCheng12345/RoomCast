package com.williamcheng.roomcast;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.classes.AlarmBuilder;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.User;
import java.util.Calendar;

public class NotificationBroadcastReceiver  extends BroadcastReceiver {
    private String title, body;
    private long time, interval;
    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        title = intent.getStringExtra("Title");
        body = intent.getStringExtra("Body");
        id = intent.getIntExtra("Id", 0);
        interval = intent.getLongExtra("Interval", 0);
        time = intent.getLongExtra("Time", 0);

        displayNotification(context);

        if (interval == Interval.HOURLY || interval == Interval.DAILY || interval == Interval.WEEKLY) {
            updateUpcomingNotification(findTriggerTime());
        }
        else if(interval == Interval.MONTHLY_START || interval == Interval.MONTHLY_END) {
            AlarmBuilder alarmBuilder = new AlarmBuilder(context);

            alarmBuilder.build(updateUpcomingNotification(findNextMonthlyAlarmTriggerTime()));
        }

    }

    private void displayNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "RoomCastNotificationChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(id, builder.build());
    }

    private UpcomingNotification updateUpcomingNotification(long newTriggerTime) {
        Message message = new Message(title, body, interval);
        UpcomingNotification upcomingNotification = new UpcomingNotification(message, id, newTriggerTime);

        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");
        String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootUsers.child(currUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);

                for(UpcomingNotification notification : user.getUpcomingNotifications()) {
                    if(notification.getId() == upcomingNotification.getId()) {
                        notification.setTriggerTime(newTriggerTime);
                        rootUsers.child(currUserID).child("upcomingNotifications").setValue(user.getUpcomingNotifications());
                    }
                }


            }
        });

        return  upcomingNotification;
    }

    private long findTriggerTime() {
        long currTime = System.currentTimeMillis();
        long numOfInterval = (currTime - time)/interval + 1;

        return time + numOfInterval*interval;
    }

    private long findNextMonthlyAlarmTriggerTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.MILLISECOND);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.MILLISECOND, sec);

        if (interval == Interval.MONTHLY_START) {
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        else {
            if(calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                calendar.add(Calendar.MONTH, 1);
            }

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        return calendar.getTimeInMillis();
    }
}
