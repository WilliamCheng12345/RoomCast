package com.williamcheng.roomcast.classes;


public class DownstreamHttpMessage {
    private final String to;
    private final Message data;

   public DownstreamHttpMessage(String to, Message data) {
       this.to = to;
       this.data = data;
   }
}
