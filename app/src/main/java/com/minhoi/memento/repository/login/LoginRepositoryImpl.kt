package com.minhoi.memento.repository.login

import com.minhoi.memento.data.dto.login.LoginRequest
import com.minhoi.memento.data.model.safeFlow
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.AuthService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : LoginRepository {

    override fun signIn(loginRequest: LoginRequest) = safeFlow {
        RetrofitClient.getInstance().create(AuthService::class.java).signIn(loginRequest)
    }
}
