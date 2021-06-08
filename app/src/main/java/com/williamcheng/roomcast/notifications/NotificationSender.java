package com.williamcheng.roomcast.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williamcheng.roomcast.classes.Interval;
import com.williamcheng.roomcast.classes.Message;
import com.williamcheng.roomcast.classes.Roommates;
import com.williamcheng.roomcast.classes.ToastBuilder;
import com.williamcheng.roomcast.classes.User;
import com.williamcheng.roomcast.notifications.Client;
import com.williamcheng.roomcast.notifications.DownstreamHttpMessage;
import com.williamcheng.roomcast.notifications.DownstreamMessageResponse;
import com.williamcheng.roomcast.notifications.HttpApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSender {
    private final Context context;
    private final HttpApi httpService;
    private final ToastBuilder toastBuilder;

    public NotificationSender(Context context) {
        this.context = context;
        toastBuilder = new ToastBuilder(context);
        httpService =  Client.getClient("https://fcm.googleapis.com/").create(HttpApi.class);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Reminder from your roommates to you";
            NotificationChannel channel = new NotificationChannel("RoomCastNotificationChannel", "RoomCast", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendToRoommates(String title, String body, String selectedItem) {
        if(title.equals("") || body.equals("")) {
            toastBuilder.createToast("Title or body cannot be empty");
            return;
        }

        String currUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        long interval = convertSelectedToInterval(selectedItem);

        root.child("Users").child(currUserUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);

                root.child("Groups").child(user.getRoommatesName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Roommates roommates = task.getResult().getValue(Roommates.class);

                        for(String receiverUID : roommates.getUsersId()) {
                            root.child("Users").child(receiverUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    User receiver = task.getResult().getValue(User.class);

                                    sendNotification(title, body, interval, receiver.getDeviceToken());
                                }
                            });
                        }
                    }
                });
            }
        });
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

    private void sendNotification(String title, String body, long interval,  String receiverToken) {
        Message message = new Message(title, body, interval);
        DownstreamHttpMessage notification = new DownstreamHttpMessage(receiverToken, message);

        httpService.sendNotification(notification).enqueue(new Callback<DownstreamMessageResponse>() {
            @Override
            public void onResponse(Call<DownstreamMessageResponse> call, Response<DownstreamMessageResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        toastBuilder.createToast("Failed to send  message");
                    }
                }
            }

            @Override
            public void onFailure(Call<DownstreamMessageResponse> call, Throwable t) {

            }
        });
    }
}
