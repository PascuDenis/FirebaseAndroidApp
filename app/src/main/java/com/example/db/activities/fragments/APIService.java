package com.example.db.activities.fragments;

import com.example.db.entity.notification.MyResponse;
import com.example.db.entity.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAywTCfpI:APA91bGAizVK9VaK1VY5gMTneUImTMmPciIBHmuxY7GpGd_MKsPMJkn45jFYbgm8SKdmk-Qh4a-g9Roo7tYWVpzf6QTHgNuw0MR5aNvVrs3e6MvaW_B26dQ7wZHj2X2i1Y5s37f1mJyn"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
