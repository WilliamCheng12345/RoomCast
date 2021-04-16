package com.williamcheng.roomcast;


public class Message {
    private String title;
    private String body;
    private int interval;

    public Message(String title, String body, int interval) {
        this.title = title;
        this.body = body;
        this.interval = interval;
    }
}
