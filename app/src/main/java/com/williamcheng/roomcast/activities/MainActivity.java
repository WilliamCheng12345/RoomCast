package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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

        Button loginButton = findViewById(R.id.main_loginButton);
        TextView signUpText = findViewById(R.id.main_signUpText);
        EditText emailInput = findViewById(R.id.main_emailInput);
        EditText passwordInput = findViewById(R.id.main_passwordInput);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                deviceToken = task.getResult();
            }
        });

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

    private void moveToNextActivity() {
        String currUserId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        rootUsers.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);

                /* If user signs into his account on a different device, we need to get the new
                 * registration token for that device in order for user to receive notifications */
                rootUsers.child(currUserId).child("token").setValue(deviceToken);

                if(user.getRoommatesName().equals("EMPTY")) {
                    startActivity(new Intent(MainActivity.this, NoRoommatesActivity.class));
                }
                else {
                    for(UpcomingNotification upcomingNotification : user.getUpcomingNotifications()) {
                        restartAlarm(upcomingNotification);
                    }

                    startActivity(new Intent(MainActivity.this, RoommatesActivity.class));
                }

                finish();
            }
        });
    }

    /* Alarms on the phone will be removed when user logs out or the phone powers off.
     * Therefore it is needed to restart these alarms when the user logs back on.*/
    private void restartAlarm(UpcomingNotification upcomingNotification) {
        final AlarmBuilder alarmBuilder = new AlarmBuilder(this);

        alarmBuilder.build(upcomingNotification.getTriggerTime(), upcomingNotification);
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