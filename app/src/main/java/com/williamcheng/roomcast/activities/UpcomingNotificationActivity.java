package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.UpcomingNotificationAdaptor;
import com.williamcheng.roomcast.classes.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingNotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_notification);

        recyclerView = findViewById(R.id.saved_notification_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        displayUpcomingNotifications();

    }

    private void displayUpcomingNotifications() {
        String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        rootUsers.child(currUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);

                UpcomingNotificationAdaptor upcomingNotificationAdaptor = new UpcomingNotificationAdaptor(UpcomingNotificationActivity.this,
                        new ArrayList<>(user.getUpcomingNotifications()));


                recyclerView.setAdapter(upcomingNotificationAdaptor);
            }
        });
    }

}