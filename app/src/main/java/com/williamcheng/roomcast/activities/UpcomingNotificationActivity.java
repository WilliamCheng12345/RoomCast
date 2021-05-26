package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.AlarmRemover;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.UpcomingNotificationAdaptor;
import com.williamcheng.roomcast.classes.User;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UpcomingNotificationActivity extends AppCompatActivity {
    private DatabaseReference rootUsers;
    private DatabaseReference rootGroups;
    private String currUserId;
    private Roommates roommates;
    private User user;
    private ActionBarDrawerToggle toggle;
    private UpcomingNotificationAdaptor upcomingNotificationAdaptor;
    private AlarmRemover alarmRemover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_notification);

        alarmRemover = new AlarmRemover(this);
        currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        rootUsers = root.child("Users");
        rootGroups = root.child("Groups");

        displayUpcomingNotifications();
        setActionBarDrawerToggle();
        setNavigationMenuItemListener();
        setJoinCode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu__upcoming_notification, menu);
        return true;
    }

    private void displayUpcomingNotifications() {
        rootUsers.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userTask) {
                user = userTask.getResult().getValue(User.class);

                upcomingNotificationAdaptor = new UpcomingNotificationAdaptor(UpcomingNotificationActivity.this,
                        new ArrayList<>(user.getUpcomingNotifications()));

                RecyclerView recyclerView = findViewById(R.id.upcoming_notification_recyclerView);
                recyclerView.setAdapter(upcomingNotificationAdaptor);
            }
        });
    }

    private void setActionBarDrawerToggle() {
        DrawerLayout drawerLayout = findViewById(R.id.upcoming_notification);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setNavigationMenuItemListener() {
        NavigationView navigationMenu = findViewById(R.id.upcoming_notification_navigationView);

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedId = item.getItemId();

                if (selectedId == R.id.menu_upcoming_notification_leave) {
                    leave();
                } else if (selectedId == R.id.menu_upcoming_notification_logOut) {
                    logOut();
                } else if (selectedId == R.id.menu_upcoming_notification_sendNotifications) {
                    startActivity(new Intent(UpcomingNotificationActivity.this, RoommatesActivity.class));
                }

                return true;
            }
        });
    }

    private void setJoinCode() {
        rootUsers.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
        roommates.getUsersId().remove(currUserId);
        rootUsers.child(currUserId).child("roommatesName").setValue("EMPTY");
        rootUsers.child(currUserId).child("upcomingNotifications").setValue(new ArrayList<>());
        rootGroups.child(roommates.getName()).child("usersId").setValue(roommates.getUsersId());

        stopAllUpcomingNotifications();

        Intent newIntent = new Intent(this, NoRoommatesActivity.class);

        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
    }

    private void logOut() {
        stopAllUpcomingNotifications();

        Intent newIntent = new Intent(this, MainActivity.class);

        FirebaseAuth.getInstance().signOut();
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
    }

    private void stopAllUpcomingNotifications() {
        for(UpcomingNotification upcomingNotification : user.getUpcomingNotifications()) {
            alarmRemover.remove(upcomingNotification);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        else if(item.getItemId() == R.id.action_bar_menu_upcoming_notification_delete) {
            stopSelectedUpcomingNotifications();
        }
        else if(item.getItemId() == R.id.action_bar_menu_upcoming_notification_selectAll) {
            upcomingNotificationAdaptor.selectAll();
        }

        return super.onOptionsItemSelected(item);
    }

    private void stopSelectedUpcomingNotifications() {
        List<UpcomingNotification> upcomingNotifications = upcomingNotificationAdaptor.getUpcomingNotifications();

        for(int i = 0; i < upcomingNotifications.size(); i++) {
            if(upcomingNotifications.get(i).isSelected()) {
                alarmRemover.remove(upcomingNotifications.get(i));
                upcomingNotificationAdaptor.removeSelectedNotification(i);
            }
        }

        rootUsers.child(currUserId).child("upcomingNotifications").setValue(upcomingNotifications);
    }

}