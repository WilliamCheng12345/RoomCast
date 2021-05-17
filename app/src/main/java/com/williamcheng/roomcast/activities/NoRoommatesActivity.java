package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.AlarmRemover;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.User;

public class NoRoommatesActivity extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    private final ToastBuilder toastBuilder = new ToastBuilder(this);
    private String currUserId;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_roommates);

        currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        root = FirebaseDatabase.getInstance().getReference();

        setActionBarDrawerToggle();
        setNavigationMenuItemListener();
    }

    private void setActionBarDrawerToggle() {
        DrawerLayout drawerLayout = findViewById(R.id.no_roommates_drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    private void setNavigationMenuItemListener() {
        NavigationView navigationMenu = findViewById(R.id.no_roommates_navigationView);

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_no_roommates_leave) {
                    logOut();
                }
                else {
                    createDialog(item.getItemId());
                    startActivity( new Intent(NoRoommatesActivity.this, RoommatesActivity.class));
                    finish();
                }
                return true;
            }
        });
    }

    private void createDialog(int selectedItemId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        if(selectedItemId == R.id.menu_no_roommates_create) {
            View view = getLayoutInflater().inflate(R.layout.dialog_no_roommates_create, null);
            alertDialog.setTitle("Create a group").setView(view);

            alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText roommates = view.findViewById(R.id.dialog_no_roommates_create_roommatesName);
                    createRoommates(roommates.getText().toString());
                }
            });
        }
        else if(selectedItemId == R.id.menu_no_roommates_join) {
            View view = getLayoutInflater().inflate(R.layout.dialog_no_roommates_join, null);
            alertDialog.setTitle("Join a group").setView(view);

            alertDialog.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText joinCodeInput = view.findViewById(R.id.dialog_no_roommates_join_joinCode);
                    joinRoommates(joinCodeInput.getText().toString());
                }
            });
        }

        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void createRoommates(String name) {
        if(name.equals("")) {
            toastBuilder.createToast("Roommates name cannot be empty");
        }

        Roommates newRoommates = new Roommates(name);

        newRoommates.addUser(currUserId);
        root.child("Groups").child(name).setValue(newRoommates);
        root.child("Users").child(currUserId).child("roommatesName").setValue(name);
    }

    private void joinRoommates(String joinCode) {
        if(joinCode.equals("")) {
            toastBuilder.createToast("Join code cannot be empty");
        }

        Query query = root.child("Groups").orderByChild("joinCode").equalTo(joinCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Roommates roommates = data.getValue(Roommates.class);
                    String roommatesName = roommates.getName();

                    roommates.addUser(currUserId);
                    root.child("Users").child(currUserId).child("roommatesName").setValue(roommatesName);
                    root.child("Groups").child(roommatesName).child("usersUID").setValue(roommates.getUsersUID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logOut() {
        AlarmRemover alarmRemover = new AlarmRemover(this);

        root.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);

                for(UpcomingNotification upcomingNotification : user.getUpcomingNotifications()) {
                    alarmRemover.remove(upcomingNotification);
                }

                Intent newIntent = new Intent(NoRoommatesActivity.this, MainActivity.class);

                FirebaseAuth.getInstance().signOut();
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}