package com.minhoi.memento.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val loginRepository = LoginRepository()

    private var _email = MutableLiveData<String>()
    private var _password = MutableLiveData<String>()

    fun onEmailTextChanged(text: CharSequence) {
        _email.value = text.toString()
    }

    fun onPasswordTextChanged(text: CharSequence) {
        _password.value = text.toString()
    }

    suspend fun signIn() {
        val email = _email.value.toString()
        val password = _password.value.toString()

        val loginRequest = LoginRequest(email, password)
        viewModelScope.launch {
            val response = loginRepository.signIn(loginRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    MentoApplication.prefs.setAccessToken(it.accessToken)
                    MentoApplication.prefs.setRefreshToken(it.refreshToken)
                }
            }
        }
    }

}