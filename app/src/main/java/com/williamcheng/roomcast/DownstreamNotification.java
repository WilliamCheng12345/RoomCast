package com.williamcheng.roomcast;

public class DownstreamNotification {
    private String to;
    private Message data;

   public DownstreamNotification(String to, Message data) {
       this.to = to;
       this.data = data;
   }
}
