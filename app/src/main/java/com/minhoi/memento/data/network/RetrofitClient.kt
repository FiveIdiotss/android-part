package com.minhoi.memento.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.minhoi.memento.BuildConfig
import com.minhoi.memento.data.network.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val baseUrl = "http://" + BuildConfig.SERVER_IP + ":8080/"
    private val gson: Gson = GsonBuilder().setLenient().create()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

    private fun createRetrofitBuilder(baseUrl: String, client: OkHttpClient? = null): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .apply { client?.let { client(it) } }

    private val getLoggedOutRetrofit = createRetrofitBuilder(baseUrl).build()

    private val getLoggedInRetrofit = createRetrofitBuilder(baseUrl, okHttpClient).build()

    fun getInstance(): Retrofit = getLoggedOutRetrofit

    fun getLoggedInInstance(): Retrofit = getLoggedInRetrofit

}