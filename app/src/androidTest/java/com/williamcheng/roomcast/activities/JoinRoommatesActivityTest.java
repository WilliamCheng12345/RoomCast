package com.williamcheng.roomcast.activities;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.Roommates;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class JoinRoommatesActivityTest {

    @Rule
    public ActivityScenarioRule<JoinRoommatesActivity> activityRule =
            new ActivityScenarioRule<>(JoinRoommatesActivity.class);

    private ActivityScenario joinRoommatesActivity;

    @Before
    public void setUp() throws Exception {
        joinRoommatesActivity = activityRule.getScenario();
    }

    @Test
    public void testJoinRoommates() {
        joinRoommatesActivity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText joinCodeInput = activity.findViewById(R.id.join_roommates_joinCode);
                Button joinButton = activity.findViewById(R.id.join_roommates_joinButton);
                DatabaseReference rootGroups = FirebaseDatabase.getInstance().getReference("Groups");
                DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Groups");
                Roommates testRoommates = new Roommates("Test");

                rootGroups.child(testRoommates.getName()).setValue(testRoommates);
                joinCodeInput.setText(testRoommates.getJoinCode());
                joinButton.performClick();


            }
        });
    }

    @After
    public void tearDown() throws Exception {
    }
}