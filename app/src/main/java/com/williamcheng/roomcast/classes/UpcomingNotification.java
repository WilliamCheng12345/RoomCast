package com.williamcheng.roomcast.classes;

public class UpcomingNotification {
    private Message message = new Message();
    private int id;
    private long triggerTime;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }
}
