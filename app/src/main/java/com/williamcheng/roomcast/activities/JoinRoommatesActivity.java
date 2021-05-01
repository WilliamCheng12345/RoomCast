package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.Roommates;

public class JoinRoommatesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_roommates);

        Button joinButton = findViewById(R.id.join_roommates_joinButton);
        EditText joinCodeInput = findViewById(R.id.join_roommates_joinCode);
        String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(joinCodeInput.getText().toString(), currUserId);
            }
        });
    }

    private void join(String joinCode, String currUserId) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("joinCode").equalTo(joinCode);
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Roommates currRoommates = data.getValue(Roommates.class);
                    String currRoommatesName = currRoommates.getName();

                    currRoommates.addUser(currUserId);
                    root.child("Users").child(currUserId).child("roommatesName").setValue(currRoommatesName);
                    root.child("Groups").child(currRoommatesName).child("usersUID").setValue(currRoommates.getUsersUID());
                }

                startActivity(new Intent(JoinRoommatesActivity.this, RoommatesActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("JoinRoommatesActivity", error.getMessage());
            }
        });
    }
}