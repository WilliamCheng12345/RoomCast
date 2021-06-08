package com.williamcheng.roomcast.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        TextView title, triggerTime, interval;
        CheckBox checkBox;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.list_upcoming_notification_title);
            triggerTime = view.findViewById(R.id.list_upcoming_notification_triggerTime);
            interval = view.findViewById(R.id.list_upcoming_notification_interval);
            checkBox = view.findViewById(R.id.list_upcoming_notification_checkBox);

        }

        public void setCheckBoxListener(View.OnClickListener checkBoxListener) {
            checkBox.setOnClickListener(checkBoxListener);
        }

        public  void setViewClickListener(View.OnClickListener viewClickListener) {
            view.setOnClickListener(viewClickListener);
        }

    }

    public UpcomingNotificationAdaptor(Context context, ArrayList<UpcomingNotification> upcomingNotifications) {
        this.context = context;
        this.upcomingNotifications = upcomingNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_upcoming_notification, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingNotificationAdaptor.ViewHolder holder, int position) {
        UpcomingNotification currUpcomingNotification = upcomingNotifications.get(position);
        Message message = currUpcomingNotification.getMessage();

        holder.title.setText(message.getTitle());

        Date date = new Date(currUpcomingNotification.getTriggerTime());
        SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy HH:mm");
        String formattedDate = format.format(date);

        holder.triggerTime.setText(formattedDate);

        holder.interval.setText(Interval.toString(message.getInterval()));

        holder.checkBox.setChecked(currUpcomingNotification.isSelected());

        holder.setViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessageDialog(message);
            }
        });

        holder.setCheckBoxListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBoxInput = (CheckBox) v;

                if(checkBoxInput.isChecked()) {
                    currUpcomingNotification.setSelected(true);
                }
                else {
                    currUpcomingNotification.setSelected(false);
                }
            }
        });
    }

    private void createMessageDialog(Message message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_upcoming_notification, null);
        TextView body = view.findViewById(R.id.list_upcoming_notification_body);
        body.setText(message.getBody());
        alertDialog.setTitle(message.getTitle()).setView(view);

        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return upcomingNotifications.size();
    }

    public void removeSelectedNotification(int position) {
        upcomingNotifications.remove(position);
        notifyItemRemoved(position);
    }

    public void selectAll() {
        for(UpcomingNotification upcomingNotification : upcomingNotifications) {
            upcomingNotification.setSelected(true);
        }

        notifyDataSetChanged();
    }

    public List<UpcomingNotification> getUpcomingNotifications() {
        return upcomingNotifications;
    }
}
