
package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.NotificationBroadcastReceiver;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.AlarmRemover;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.SavedNotification;
import com.williamcheng.roomcast.classes.User;

import java.util.ArrayList;

public class RoommatesActivity extends AppCompatActivity {
    User currUser;
    Roommates currRoommates;
    String currUserUID;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);

        Button leaveButton = findViewById(R.id.roommates_leaveButton);
        Button sendNotificationButton = findViewById(R.id.roommates_sendNotificationButton);
        Button savedNotificationButton = findViewById(R.id.roommates_savedNotificationButton);
        Button logOutButton = findViewById(R.id.roommates_logOutButton);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();
        currUserUID = firebaseAuth.getCurrentUser().getUid();

        root.child("Users").child(currUserUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userTask) {
                currUser = userTask.getResult().getValue(User.class);

                root.child("Groups").child(currUser.getRoommatesName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> groupTask) {
                        currRoommates = groupTask.getResult().getValue(Roommates.class);
                        TextView joinCode = findViewById(R.id.roommates_joinCode);
                        joinCode.setText(currRoommates.getJoinCode());
                    }
                });

                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leave();
                    }
                });

                logOutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logOut();
                    }
                });
            }
        });

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        savedNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoommatesActivity.this, SavedNotificationActivity.class));
            }
        });
    }

    private void leave() {
        currRoommates.getUsersUID().remove(currUserUID);
        root.child("Users").child(currUserUID).child("roommatesName").setValue("EMPTY");
        root.child("Users").child(currUserUID).child("savedNotifications").setValue(new ArrayList<>());
        root.child("Groups").child(currRoommates.getName()).child("usersUID").setValue(currRoommates.getUsersUID());
        startActivity(new Intent(RoommatesActivity.this, NoRoommatesActivity.class));
    }

    private void sendNotification() {
        startActivity(new Intent(RoommatesActivity.this, NotificationActivity.class));
    }

    private void logOut() {
        AlarmRemover alarmRemover = new AlarmRemover(this);

        for(SavedNotification savedNotification : currUser.getSavedNotifications()) {
            alarmRemover.remove(savedNotification);
        }

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}