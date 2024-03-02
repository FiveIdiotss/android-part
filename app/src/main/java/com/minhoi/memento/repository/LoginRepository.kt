package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import kotlinx.coroutines.flow.flow

class LoginRepository {
    private val retrofitClient = RetrofitClient.getInstance().create(APIService::class.java)

    suspend fun signIn(loginRequest: LoginRequest) = flow {
        val response = retrofitClient.signIn(loginRequest)
        if (response.isSuccessful) {
            emit(response)
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }

}
