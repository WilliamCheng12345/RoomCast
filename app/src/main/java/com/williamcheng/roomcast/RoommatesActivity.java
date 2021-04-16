
package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoommatesActivity extends AppCompatActivity {
    User currUser;
    Roommates currRoommates;
    String currUserUID;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);

        TextView joinCode = findViewById(R.id.roommates_joinCode);
        Button leaveButton = findViewById(R.id.roommates_leaveButton);
        Button sendNotificationButton = findViewById(R.id.roommates_sendNotificationButton);
        Button savedNotificationButton = findViewById(R.id.roommates_savedNotificationButton);

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

                        joinCode.setText(currRoommates.getJoinCode());
                    }
                });

                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leave();
                    }
                });

                sendNotificationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendNotification();
                    }
                });
            }
        });

        savedNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currIntent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent currPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, currIntent,0);
                alarmManager.cancel(currPendingIntent);

                startActivity(new Intent(RoommatesActivity.this, SavedNotificationActivity.class));
            }
        });
    }

    private void leave() {
        currRoommates.getUsersUID().remove(currUserUID);

        root.child("Users").child(currUserUID).child("roommatesName").setValue("EMPTY");
        root.child("Groups").child(currRoommates.getName()).child("usersUID").setValue(currRoommates.getUsersUID());

        startActivity(new Intent(RoommatesActivity.this, NoRoommatesActivity.class));
    }

    private void sendNotification() {
        startActivity(new Intent(RoommatesActivity.this, NotificationActivity.class));
    }
}