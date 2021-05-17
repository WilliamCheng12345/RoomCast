package com.williamcheng.roomcast.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.williamcheng.roomcast.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpcomingNotificationAdaptor extends RecyclerView.Adapter<UpcomingNotificationAdaptor.ViewHolder> {
    private final Context context;
    private final ArrayList<UpcomingNotification> upcomingNotifications;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, body, triggerTime, interval;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.list_saved_notification_title);
            body = view.findViewById(R.id.list_saved_notification_body);
            triggerTime = view.findViewById(R.id.list_saved_notification_triggerTime);
            interval = view.findViewById(R.id.list_saved_notification_interval);
        }
    }

    public UpcomingNotificationAdaptor(Context context, ArrayList<UpcomingNotification> upcomingNotifications) {
        this.context = context;
        this.upcomingNotifications = upcomingNotifications;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_saved_notifications, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingNotificationAdaptor.ViewHolder holder, int position) {
        UpcomingNotification currUpcomingNotification = upcomingNotifications.get(position);
        Message currMessage = currUpcomingNotification.getMessage();

        holder.title.setText(currMessage.getTitle());
        holder.body.setText(currMessage.getBody());


        Date date = new Date(currUpcomingNotification.getTriggerTime());
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = format.format(date);
        System.out.println(formattedDate);

        holder.triggerTime.setText(formattedDate);

        holder.interval.setText(String.valueOf(currMessage.getInterval()));
    }

    @Override
    public int getItemCount() {
        return upcomingNotifications.size();
    }
}
