package com.minhoi.memento.data.network.service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationService {
    @POST("api/fcm")
    fun saveToken(@Query("myToken") token: String) : Call<String>
}