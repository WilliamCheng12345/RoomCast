package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private HttpApi httpService;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpService = Client.getClient("https://fcm.googleapis.com/").create(HttpApi.class);
    }

    public void send(View view) {
        EditText messageTitle = findViewById(R.id.messageTitle);
        EditText messageBody = findViewById(R.id.messageBody);
        String title = messageTitle.getText().toString();
        String body = messageBody.getText().toString();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                userToken = task.getResult();
            }
        });

        sendNotification(title, body, userToken);
    }

    public void sendNotification(String title, String body, String receiverToken) {
        Data message = new Data(title, body);
        DownstreamMessage sender = new DownstreamMessage(receiverToken, message);
        httpService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        Toast.makeText(MainActivity.this, "Failed to send", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}