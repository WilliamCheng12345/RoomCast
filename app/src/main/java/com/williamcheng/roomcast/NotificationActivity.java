package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private HttpApi httpService;
    private String receiverToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        httpService = Client.getClient("https://fcm.googleapis.com/").create(HttpApi.class);
    }

    public void send(View view) {
        EditText messageTitle = findViewById(R.id.messageTitle);
        EditText messageBody = findViewById(R.id.messageBody);
        String title = messageTitle.getText().toString();
        String body = messageBody.getText().toString();

      Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo("ypcheng.cheng@gmail.com");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    receiverToken = data.getValue(User.class).getToken();
                }

                sendNotification(title, body, receiverToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void sendNotification(String title, String body, String receiverToken) {
        Message message = new Message(title, body);
        DownstreamNotification notification = new DownstreamNotification(receiverToken, message);
        httpService.sendNotification(notification).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        Toast.makeText(NotificationActivity.this, "Failed to send", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }
}