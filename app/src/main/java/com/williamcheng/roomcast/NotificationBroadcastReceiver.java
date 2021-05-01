package com.williamcheng.roomcast;

import android.app.Notification;
import android.app.NotificationManager;
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
import com.williamcheng.roomcast.classes.SavedNotification;
import com.williamcheng.roomcast.classes.User;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class NotificationBroadcastReceiver  extends BroadcastReceiver {
    private String title;
    private String body;
    private long time;
    private int id;
    private long interval;
    private AlarmBuilder alarmBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmBuilder = new AlarmBuilder(context);
        title = intent.getStringExtra("Title");
        body = intent.getStringExtra("Body");
        id = intent.getIntExtra("Id", 0);
        interval = intent.getLongExtra("Interval", 0);
        time = intent.getLongExtra("Time", 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "RoomCastNotificationChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(id, builder.build());

        if(interval == Interval.MONTHLY_START || interval == Interval.MONTHLY_END) {
            createNextMonthlyAlarm();
        }

        System.out.println("Broadcast received");
    }

    private void createNextMonthlyAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, 1);
        if(interval == Interval.MONTHLY_START) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        else {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        long triggerTime = calendar.getTimeInMillis();

        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");
        String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Message message = new Message(title, body, interval);
        SavedNotification savedNotification = new SavedNotification(message, id, triggerTime);

        rootUsers.child(currUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User currUser = task.getResult().getValue(User.class);
                for(SavedNotification notification : currUser.getSavedNotifications()) {
                    if(notification.getId() == savedNotification.getId()) {
                        currUser.getSavedNotifications().remove(notification);
                    }
                }

                currUser.getSavedNotifications().add(savedNotification);
                rootUsers.child(currUserID).child("savedNotifications").setValue(currUser.getSavedNotifications());
                alarmBuilder.build(triggerTime, savedNotification);
            }
        });



    }


}
