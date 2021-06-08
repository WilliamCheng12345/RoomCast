package com.williamcheng.roomcast.notifications;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.williamcheng.roomcast.alarms.AlarmBuilder;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.User;

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
        long interval = Long.parseLong(remoteMessage.getData().get("interval"));

        buildAlarmForNotification(title, body, interval);
    }

    private void buildAlarmForNotification(String title, String body, long interval) {
        //In case the user logs out before the notifications can be sent.
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
             return;
        }

        final AlarmBuilder alarmBuilder = new AlarmBuilder(this);
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");
        String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootUsers.child(currUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);
                long currTime = System.currentTimeMillis();
                final int id = (Long.toString(currTime)).hashCode();
                Message message = new Message(title, body, interval);
                UpcomingNotification upcomingNotification = new UpcomingNotification(message, id, currTime);

                if(interval != Interval.ONCE) {
                    user.getUpcomingNotifications().add(upcomingNotification);
                    rootUsers.child(currUserID).child("upcomingNotifications").setValue(user.getUpcomingNotifications());
                }

                alarmBuilder.build(upcomingNotification);
            }
        });
    }
}
