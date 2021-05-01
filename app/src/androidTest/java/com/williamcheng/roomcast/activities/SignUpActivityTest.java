package com.williamcheng.roomcast.activities;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule =
            new ActivityScenarioRule<>(SignUpActivity.class);

    private ActivityScenario signUpActivity;

    @Before
    public void setUp() throws Exception {
        signUpActivity = activityRule.getScenario();
    }

    @Test
    public void testSignIn() {
        String email = "williamcheng98@gmil.com";
        String password = "1234567";
        signUpUser(email, password);
        signInUser(email, password);

        email = "";
        password = "";
        signUpUser(email, password);
    }


    public void signUpUser(String email, String password) {
        signUpActivity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText emailInput = activity.findViewById(R.id.sign_up_emailInput);
                EditText passwordInput = activity.findViewById(R.id.sign_up_passwordInput);
                Button signUpButton = activity.findViewById(R.id.sign_up_signUpButton);

                emailInput.setText(email);
                passwordInput.setText(password);
                signUpButton.performClick();
            }
        });
    }

    public void signInUser(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    assertEquals(firebaseAuth.getCurrentUser().getEmail(), email);
                    rootUsers.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                    firebaseAuth.getCurrentUser().delete();
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch(Exception e) {
                        Log.e("SignActivityTest", e.getMessage());
                    }
                }

            }
        });
    }

    @After
    public void tearDown() throws Exception {
    }
}