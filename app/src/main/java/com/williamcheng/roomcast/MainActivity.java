package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
     private ToastBuilder toastBuilder = new ToastBuilder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.main_loginButton);
        Button signUpButton = findViewById(R.id.main_signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void login() {
        EditText emailInput = findViewById(R.id.main_emailInput);
        EditText passwordInput = findViewById(R.id.main_passwordInput);
        String userEmail = emailInput.getText().toString();
        String userPassword = passwordInput.getText().toString();

        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() ) {
                    String currUserUID = firebaseAuth.getCurrentUser().getUid();

                    rootUsers.child(currUserUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            User currUser = task.getResult().getValue(User.class);

                            if(currUser.getRoommatesName().equals("EMPTY")) {
                                startActivity(new Intent(MainActivity.this, NoRoommatesActivity.class));
                            }
                            else {
                                startActivity(new Intent(MainActivity.this, RoommatesActivity.class));
                            }
                        }
                    });
                }
                else {
                   displayLoginError(task);
                }
            }
        });
    }

    private void signUp() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
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
            Log.e("MainActivity Error: ", e.getMessage());
        }
    }

}