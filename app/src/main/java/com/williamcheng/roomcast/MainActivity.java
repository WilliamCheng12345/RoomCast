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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String userEmail;
    String userToken;
    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login);
        Button signUpButton = findViewById(R.id.signUp);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                userToken = task.getResult();
            }
        });

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
        EditText emailInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);
        userEmail = emailInput.getText().toString();
        userPassword = passwordInput.getText().toString();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        DatabaseReference users = root.getReference("Users");

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if(firebaseAuth.getCurrentUser() != null) {
                        User user = new User(userEmail, userToken);
                        users.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                    }

                    startActivity(new Intent(MainActivity.this, CreateRoommatesActivity.class));
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                    catch(FirebaseAuthInvalidUserException e) {
                        Toast.makeText(MainActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Log.e("MainActivity Error: ", e.getMessage());
                    }
                }
            }
        });
    }

    private void signUp() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }

}