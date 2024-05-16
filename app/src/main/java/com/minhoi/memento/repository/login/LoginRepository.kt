package com.minhoi.memento.repository.login

import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.dto.LoginResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface LoginRepository {
    fun signIn(loginRequest: LoginRequest): Flow<Response<LoginResponse>>

    fun checkLoginState(token: String): Flow<ApiResult<String>>
}