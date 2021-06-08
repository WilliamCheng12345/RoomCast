package com.williamcheng.roomcast.classes;

import com.williamcheng.roomcast.classes.Message;

public class UpcomingNotification {
    private Message message = new Message();
    private int id;
    private long triggerTime;
    private boolean isSelected;

    public UpcomingNotification(Message message, int id, long triggerTime) {
        this.message = message;
        this.id = id;
        this.triggerTime = triggerTime;
    }

    public UpcomingNotification() {

    }

    public Message getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
