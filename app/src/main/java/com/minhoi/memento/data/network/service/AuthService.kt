package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.dto.LoginResponse
import com.minhoi.memento.data.dto.TokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("api/member/signIn")
    suspend fun signIn(
        @Body loginRequest: LoginRequest
    ): Response<CommonResponse<LoginResponse>>

    @GET("api/refresh")
    suspend fun getAccessToken(@Header("Authorization") token: String): Response<CommonResponse<TokenDto>>
}