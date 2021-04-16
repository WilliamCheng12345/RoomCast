package com.williamcheng.roomcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private HttpApi httpService;
    private EditText messageTitle;
    private EditText messageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Button sendButton = findViewById(R.id.notification_sendButton);
        messageTitle = findViewById(R.id.notification_messageTitle);
        messageBody = findViewById(R.id.notification_messageBody);

        httpService = Client.getClient("https://fcm.googleapis.com/").create(HttpApi.class);

        createNotificationChannel();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RoomCast";
            String description = "Reminder from your roommates to you";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelID = "RoomCastNotificationChannel";
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void send() {
        String title = messageTitle.getText().toString();
        String body = messageBody.getText().toString();
        String currUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        root.child("Users").child(currUserUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User currUser = task.getResult().getValue(User.class);

                root.child("Groups").child(currUser.getRoommatesName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Roommates currRoommates = task.getResult().getValue(Roommates.class);

                        for(String receiverUID : currRoommates.getUsersUID()) {
                            root.child("Users").child(receiverUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    User receiver = task.getResult().getValue(User.class);

                                    sendNotification(title, body, receiver.getToken());
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void sendNotification(String title, String body, String receiverToken) {
        Message message = new Message(title, body, Interval.ONCE);
        DownstreamNotification notification = new DownstreamNotification(receiverToken, message);

        httpService.sendNotification(notification).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        Toast.makeText(NotificationActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }
}