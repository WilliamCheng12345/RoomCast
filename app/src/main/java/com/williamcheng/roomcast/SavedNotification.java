package com.williamcheng.roomcast;

public class SavedNotification {
    private Message message;
    private String roommates;
    private long timeStamp;

    public SavedNotification(Message message, String roommates, long timeStamp) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.roommates = roommates;
    }

}
