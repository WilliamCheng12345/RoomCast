package com.williamcheng.roomcast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.williamcheng.roomcast.R;

public class NoRoommatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_roommates);

        Button createButton = findViewById(R.id.no_roommates_createButton);
        Button joinButton = findViewById(R.id.no_roommates_joinButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoRoommatesActivity.this, CreateRoommatesActivity.class));
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoRoommatesActivity.this, JoinRoommatesActivity.class));
            }
        });

    }
}