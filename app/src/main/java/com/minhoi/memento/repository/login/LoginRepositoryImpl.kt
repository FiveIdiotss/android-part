package com.minhoi.memento.repository.login

import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.AuthService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : LoginRepository {

    override fun signIn(loginRequest: LoginRequest) = flow {
        val response = RetrofitClient.getInstance().create(AuthService::class.java).signIn(loginRequest)
        if (response.isSuccessful) {
            emit(response)
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }

    override fun checkLoginState(token: String) = safeFlow {
        authService.checkLoginState(token)
    }

}
