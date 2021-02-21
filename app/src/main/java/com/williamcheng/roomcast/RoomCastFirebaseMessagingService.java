package com.williamcheng.roomcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RoomCastFirebaseMessagingService  extends FirebaseMessagingService {
    private static final String TAG = RoomCastFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "Token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Notification "+ remoteMessage.getFrom());

        if(remoteMessage.getNotification() != null) {
           String title = remoteMessage.getNotification().getTitle();
           String body = remoteMessage.getNotification().getBody();

           Log.d(TAG,  title + ": " + body);
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(body)
    }
}
