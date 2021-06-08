package com.williamcheng.roomcast.notifications;


import com.williamcheng.roomcast.classes.Message;

public class DownstreamHttpMessage {
    private final String to;
    private final Message data;

   public DownstreamHttpMessage(String to, Message data) {
       this.to = to;
       this.data = data;
   }
}
