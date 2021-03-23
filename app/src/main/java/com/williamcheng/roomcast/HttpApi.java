package com.williamcheng.roomcast;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HttpApi {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApModN4A:APA91bE0w3f-ngDxfRLtic10rPOb37OT9n7W9fKSLOBmllT5XAhAykRtAXELB646o0r4s5Or-QTAe6PBgwMue1K6MiNpp5Xf5xA1TOhTB8ed6UXDzwE7FBO0k-cy8cnK1WG21MspzTUH"
            }
    )

    @POST("fcm/send")
    Call<MessageResponse> sendNotification(@Body DownstreamNotification body);
}