package com.williamcheng.roomcast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.Client;
import com.williamcheng.roomcast.classes.DownstreamHttpMessage;
import com.williamcheng.roomcast.classes.DownstreamMessageResponse;
import com.williamcheng.roomcast.HttpApi;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.R;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private HttpApi httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Button sendButton = findViewById(R.id.notification_sendButton);

        EditText messageBody = findViewById(R.id.notification_messageBody);
        EditText messageTitle = findViewById(R.id.notification_messageTitle);
        Spinner intervalSpinner = findViewById(R.id.notification_intervalSpinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.interval, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(spinnerAdapter);
        intervalSpinner.setOnItemSelectedListener(this);

        httpService = Client.getClient("https://fcm.googleapis.com/").create(HttpApi.class);

        createNotificationChannel();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = intervalSpinner.getSelectedItem().toString();
                long interval = convertSelectedToInterval(selectedItem);

                send(messageTitle.getText().toString(), messageBody.getText().toString(), interval);
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

    private long convertSelectedToInterval(String selectedItem) {
        switch (selectedItem) {
            case "Once":
                return Interval.ONCE;
            case "Hourly":
                return Interval.HOURLY;
            case "Daily":
                return Interval.DAILY;
            case "Weekly":
                return Interval.WEEKLY;
            case "Start of Month":
                return Interval.MONTHLY_START;
            case "End of Month":
                return Interval.MONTHLY_END;
        }

        return Interval.ONCE;
    }

    private void send(String title, String body, long interval) {
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
                        Toast.makeText(NotificationActivity.this, Long.toString(interval), Toast.LENGTH_SHORT).show();

                        for(String receiverUID : currRoommates.getUsersUID()) {
                            root.child("Users").child(receiverUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    User receiver = task.getResult().getValue(User.class);

                                    sendNotification(title, body, interval, receiver.getToken());
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void sendNotification(String title, String body, long interval,  String receiverToken) {
        Message message = new Message(title, body, interval);
        DownstreamHttpMessage notification = new DownstreamHttpMessage(receiverToken, message);

        httpService.sendNotification(notification).enqueue(new Callback<DownstreamMessageResponse>() {
            @Override
            public void onResponse(Call<DownstreamMessageResponse> call, Response<DownstreamMessageResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        Toast.makeText(NotificationActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DownstreamMessageResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}