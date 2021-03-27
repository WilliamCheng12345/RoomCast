package com.williamcheng.roomcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateRoommatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roommates);

        EditText roommatesNameInput = findViewById(R.id.roommatesName);
        Button createRoommatesButton = findViewById(R.id.createRoommates);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        DatabaseReference groups = root.getReference("Groups");

        createRoommatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roommatesName = roommatesNameInput.getText().toString();
                String currUserUID = firebaseAuth.getCurrentUser().getUid();
                Roommates newRoommates = new Roommates(roommatesName, currUserUID);

                newRoommates.generateJoinCode();
                newRoommates.addUser(currUserUID);
                groups.child(roommatesName).setValue(newRoommates);
                startActivity( new Intent(CreateRoommatesActivity.this, RoommatesActivity.class));
            }
        });
    }
}