package com.williamcheng.roomcast.classes;

import com.williamcheng.roomcast.classes.Message;

public class DownstreamHttpMessage {
    private String to;
    private Message data;

   public DownstreamHttpMessage(String to, Message data) {
       this.to = to;
       this.data = data;
   }
}
