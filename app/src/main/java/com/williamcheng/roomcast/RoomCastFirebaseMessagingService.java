package com.williamcheng.roomcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.williamcheng.roomcast.classes.AlarmBuilder;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.SavedNotification;
import com.williamcheng.roomcast.classes.User;

import java.util.List;

public class RoomCastFirebaseMessagingService  extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final AlarmBuilder alarmBuilder = new AlarmBuilder(this);
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        long interval = Long.parseLong(remoteMessage.getData().get("interval"));
        long currTime = System.currentTimeMillis();
        final int id = (Long.toString(currTime)).hashCode();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");
        String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootUsers.child(currUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User currUser = task.getResult().getValue(User.class);
                Message message = new Message(title, body, interval);
                SavedNotification savedNotification = new SavedNotification(message, id, currTime);

                if(interval != Interval.ONCE) {
                    currUser.getSavedNotifications().add(savedNotification);
                    rootUsers.child(currUserID).child("savedNotifications").setValue(currUser.getSavedNotifications());
                }

                alarmBuilder.build(currTime, savedNotification);
            }
        });

    }
}
