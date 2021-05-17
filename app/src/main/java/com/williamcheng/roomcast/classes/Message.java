package com.williamcheng.roomcast.classes;


public class Message {
    private String title, body;
    private long interval;

    public Message(String title, String body, long interval) {
        this.title = title;
        this.body = body;
        this.interval = interval;
    }

    public Message() {
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getInterval() {
        return interval;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
