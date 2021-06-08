
package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.alarms.AlarmRemover;
import com.williamcheng.roomcast.notifications.NotificationSender;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.User;

import java.util.ArrayList;

public class RoommatesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private final ToastBuilder toastBuilder = new ToastBuilder(this);
    private User user;
    private Roommates roommates;
    private String userId;
    private DatabaseReference rootUsers;
    private DatabaseReference rootGroups;
    private ActionBarDrawerToggle toggle;
    private Spinner intervalSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootUsers = root.child("Users");
        rootGroups = root.child("Groups");

        setIntervalSpinner();
        setActionBarDrawerToggle();
        setNavigationMenuItemListener();
        setSendButton();
        setJoinCode();
    }

    private void setIntervalSpinner() {
        intervalSpinner = findViewById(R.id.roommates_intervalSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.interval, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(spinnerAdapter);
        intervalSpinner.setOnItemSelectedListener(this);
    }


    private void setActionBarDrawerToggle() {
        DrawerLayout drawerLayout = findViewById(R.id.roommates);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setNavigationMenuItemListener() {
        NavigationView navigationMenu = findViewById(R.id.roommates_navigationView);

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedId = item.getItemId();

                if (selectedId == R.id.menu_roommates_leave) {
                    leave();
                } else if (selectedId == R.id.menu_roommates_logOut) {
                    logOut();
                } else if (selectedId == R.id.menu_roommates_upcomingNotifications) {
                    startActivity(new Intent(RoommatesActivity.this, UpcomingNotificationActivity.class));
                }

                return true;
            }
        });
    }

    private void setSendButton() {
        EditText messageTitle = findViewById(R.id.roommates_messageTitle);
        EditText messageBody = findViewById(R.id.roommates_messageBody);
        Button sendButton = findViewById(R.id.roommates_sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(messageTitle.getText().toString(), messageBody.getText().toString(), intervalSpinner.getSelectedItem().toString());
            }
        });

    }

    private void setJoinCode() {
        rootUsers.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userTask) {
                user = userTask.getResult().getValue(User.class);

                rootGroups.child(user.getRoommatesName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> groupTask) {
                        roommates = groupTask.getResult().getValue(Roommates.class);
                        TextView joinCode = findViewById(R.id.header_roommates_joinCode);
                        TextView roommatesName = findViewById(R.id.header_roommates_name);

                        joinCode.setText(roommates.getJoinCode());
                        roommatesName.setText(roommates.getName());
                    }
                });
            }
        });
    }
    private void leave() {
        roommates.getUsersId().remove(userId);
        rootUsers.child(userId).child("roommatesName").setValue("EMPTY");
        rootUsers.child(userId).child("upcomingNotifications").setValue(new ArrayList<>());

        if(roommates.getUsersId().size() == 0) {
            rootGroups.child(roommates.getName()).removeValue(); 
        }
        else {
            rootGroups.child(roommates.getName()).child("usersId").setValue(roommates.getUsersId());
        }


        stopUpcomingNotifications();

        Intent newIntent = new Intent(this, NoRoommatesActivity.class);

        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
    }

    private void logOut() {
        stopUpcomingNotifications();

        Intent newIntent = new Intent(this, MainActivity.class);

        FirebaseAuth.getInstance().signOut();
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
    }

    private void sendNotification(String title, String body, String interval) {
        NotificationSender notificationSender = new NotificationSender(this);

        notificationSender.sendToRoommates(title, body, interval);
        toastBuilder.createToast("Notification sent");

    }


    private void stopUpcomingNotifications() {
        AlarmRemover alarmRemover = new AlarmRemover(this);

        for(UpcomingNotification upcomingNotification : user.getUpcomingNotifications()) {
            alarmRemover.remove(upcomingNotification);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}