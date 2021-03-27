package com.williamcheng.roomcast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RoommatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);



        TextView joinCode = (TextView)findViewById(R.id.joinCode);
    }
}