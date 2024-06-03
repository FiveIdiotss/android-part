package com.minhoi.memento.repository.login

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.dto.LoginResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun signIn(loginRequest: LoginRequest): Flow<ApiResult<CommonResponse<LoginResponse>>>
}