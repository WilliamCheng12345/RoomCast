package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateRoommatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roommates);

        EditText roommatesNameInput = findViewById(R.id.create_roommates_roommatesNameInput);
        Button createRoommatesButton = findViewById(R.id.create_roommates_createButton);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        createRoommatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roommatesName = roommatesNameInput.getText().toString();
                String currUserUID = firebaseAuth.getCurrentUser().getUid();
                Roommates newRoommates = new Roommates(roommatesName);

                newRoommates.generateJoinCode();
                newRoommates.addUser(currUserUID);
                root.child("Groups").child(roommatesName).setValue(newRoommates);
                root.child("Users").child(currUserUID).child("roommatesName").setValue(roommatesName);
                startActivity( new Intent(CreateRoommatesActivity.this, RoommatesActivity.class));
            }
        });
    }
}