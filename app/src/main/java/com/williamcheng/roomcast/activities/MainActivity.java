package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.AlarmBuilder;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.UpcomingNotification;
import com.williamcheng.roomcast.classes.User;

public class MainActivity extends AppCompatActivity {
     private final ToastBuilder toastBuilder = new ToastBuilder(this);
     private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
     private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(firebaseAuth.getCurrentUser() != null) {
            moveToNextActivity();
        }

        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    deviceToken = task.getResult();

                    setOnClickListeners();
                }
                else {
                    toastBuilder.createToast("Device registration token retrieval failed");
                }
            }
        });
    }

    private void moveToNextActivity() {
        String currUserId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        rootUsers.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);

                    /* If user signs into his account on a different device, we need to get the new
                     * registration token for that device in order for user to receive notifications */
                    rootUsers.child(currUserId).child("deviceToken").setValue(deviceToken);

                    if(user.getRoommatesName().equals("EMPTY")) {
                        startActivity(new Intent(MainActivity.this, NoRoommatesActivity.class));
                    }
                    else {
                        restartAlarms(user);
                        startActivity(new Intent(MainActivity.this, RoommatesActivity.class));
                    }

                    finish();
                }
            }
        });
    }

    /* Alarms on the phone will be removed when user logs out or the phone powers off.
     * Therefore it is needed to restart these alarms when the user logs back on.*/
    private void restartAlarms(User user) {
        final AlarmBuilder alarmBuilder = new AlarmBuilder(this);

        for(UpcomingNotification upcomingNotification : user.getUpcomingNotifications()) {
            alarmBuilder.build(upcomingNotification);
        }
    }

    private void setOnClickListeners() {
        Button loginButton = findViewById(R.id.main_loginButton);
        TextView signUpText = findViewById(R.id.main_signUpText);
        EditText emailInput = findViewById(R.id.main_emailInput);
        EditText passwordInput = findViewById(R.id.main_passwordInput);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void login(String email, String password) {
        if(email.equals("") || password.equals("")) {
            toastBuilder.createToast("Email or password cannot be empty");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() ) {
                    moveToNextActivity();
                }
                else {
                   displayLoginError(task);
                }
            }
        });
    }

    private void displayLoginError(Task<AuthResult> task) {
        try {
            throw task.getException();
        }
        catch (FirebaseAuthInvalidCredentialsException e) {
            toastBuilder.createToast("Password is incorrect");
        }
        catch(FirebaseAuthInvalidUserException e) {
            toastBuilder.createToast("Email does not exist");
        }
        catch (Exception e) {
            toastBuilder.createToast(e.getMessage());
        }
    }

    private void signUp() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
}