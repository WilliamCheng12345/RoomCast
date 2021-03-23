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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {
    String userEmail;
    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUpButton = findViewById(R.id.signUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        EditText emailInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);
        userEmail = emailInput.getText().toString();
        userPassword = passwordInput.getText().toString();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(SignUpActivity.this, "Password is less than 6 characters", Toast.LENGTH_SHORT).show();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(SignUpActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(SignUpActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Log.e("SignUpActivity Error: ", e.getMessage());
                    }
                }
            }
        });
    }
}