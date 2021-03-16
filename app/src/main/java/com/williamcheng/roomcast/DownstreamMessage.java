package com.williamcheng.roomcast;

public class DownstreamMessage {
    private String to;
    private Data data;

   public DownstreamMessage(String to, Data data) {
       this.to = to;
       this.data = data;
   }
}
