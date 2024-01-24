package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient

class LoginRepository {
    private val retrofitClient = RetrofitClient.getInstance().create(APIService::class.java)

    suspend fun signIn(loginRequest: LoginRequest) = retrofitClient.signIn(loginRequest)

}
