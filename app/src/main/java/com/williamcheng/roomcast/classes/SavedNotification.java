package com.williamcheng.roomcast.classes;

public class SavedNotification {
    private Message message = new Message();
    private int id;
    private long timeStamp;

    public SavedNotification(Message message, int id, long timeStamp) {
        this.message = message;
        this.id = id;
        this.timeStamp = timeStamp;
    }

    public SavedNotification() {

    }

    public Message getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
