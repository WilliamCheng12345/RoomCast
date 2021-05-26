package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.User;

public class SignUpActivity extends AppCompatActivity {
    private String deviceToken;
    private final ToastBuilder toastBuilder = new ToastBuilder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    deviceToken = task.getResult();
                    setSignUpButton();
                }
                else {
                    toastBuilder.createToast("Device registration token retrieval failed");
                }
            }
        });
    }

    private void setSignUpButton () {
        Button signUpButton = findViewById(R.id.sign_up_signUpButton);
        EditText emailInput = findViewById(R.id.sign_up_emailInput);
        EditText passwordInput = findViewById(R.id.sign_up_passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.sign_up_confirmPasswordInput);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(emailInput.getText().toString(), passwordInput.getText().toString(), confirmPasswordInput.getText().toString());
            }
        });
    }

    public void signUp(String email, String password, String confirmedPassword) {
        if(email.equals("") || password.equals("") || confirmedPassword.equals("")) {
            toastBuilder.createToast("Email or password cannot be empty");
            return;
        }

        if(!confirmedPassword.equals(password)) {
            toastBuilder.createToast("Passwords do not match");
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    User newUser = new User(email, deviceToken);
                    String newUserId = firebaseAuth.getCurrentUser().getUid();

                    rootUsers.child(newUserId).setValue(newUser);
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                }
                else {
                    displaySignUpError(task);
                }
            }
        });
    }

    private void displaySignUpError(Task<AuthResult> task) {
        try {
            throw task.getException();
        }
        catch (FirebaseAuthWeakPasswordException e) {
            toastBuilder.createToast("Password is less than 6 characters");
        }
        catch(FirebaseAuthInvalidCredentialsException e) {
            toastBuilder.createToast("Email does not exist");
        }
        catch (FirebaseAuthUserCollisionException e) {
            toastBuilder.createToast("Account already exists");
        }
        catch (Exception e) {
            toastBuilder.createToast(e.getMessage());
        }
    }
}