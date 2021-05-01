package com.williamcheng.roomcast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.Roommates;

public class CreateRoommatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roommates);

        EditText roommatesNameInput = findViewById(R.id.create_roommates_roommatesNameInput);
        Button createRoommatesButton = findViewById(R.id.create_roommates_createButton);
        String currUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        createRoommatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roommatesName = roommatesNameInput.getText().toString();
                Roommates newRoommates = new Roommates(roommatesName);

                newRoommates.addUser(currUserUID);
                root.child("Groups").child(roommatesName).setValue(newRoommates);
                root.child("Users").child(currUserUID).child("roommatesName").setValue(roommatesName);
                startActivity( new Intent(CreateRoommatesActivity.this, RoommatesActivity.class));
                finish();
            }
        });
    }
}