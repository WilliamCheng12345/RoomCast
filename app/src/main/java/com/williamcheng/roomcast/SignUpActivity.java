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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUpActivity extends AppCompatActivity {
    String userToken;
    ToastBuilder toastBuilder = new ToastBuilder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUpButton = findViewById(R.id.sign_up_signUpButton);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                userToken = task.getResult();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        EditText emailInput = findViewById(R.id.sign_up_emailInput);
        EditText passwordInput = findViewById(R.id.sign_up_passwordInput);
        String userEmail = emailInput.getText().toString();
        String userPassword = passwordInput.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference("Users");

        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    User user = new User(userEmail, userToken);
                    String currUserUID = firebaseAuth.getCurrentUser().getUid();

                    rootUsers.child(currUserUID).setValue(user);
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
            Log.e("SignUpActivity Error: ", e.getMessage());
        }
    }
}