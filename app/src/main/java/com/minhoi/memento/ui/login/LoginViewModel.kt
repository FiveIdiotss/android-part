package com.minhoi.memento.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.repository.LoginRepository
import com.minhoi.memento.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val loginRepository = LoginRepository()

    private var _email = MutableLiveData<String>()
    private var _password = MutableLiveData<String>()

    private val _loginState = MutableStateFlow<UiState>(UiState.Empty)
    val loginState = _loginState.asStateFlow()

    fun onEmailTextChanged(text: CharSequence) {
        _email.value = text.toString()
    }

    fun onPasswordTextChanged(text: CharSequence) {
        _password.value = text.toString()
    }

    fun signIn() {
        val email = _email.value.toString()
        val password = _password.value.toString()

        val loginRequest = LoginRequest(email, password)
        viewModelScope.launch {
            loginRepository.signIn(loginRequest)
                .onStart {
                    _loginState.value = UiState.Loading
                }
                .catch { e ->
                    _loginState.value = UiState.Error(e)
                }
                .collectLatest { response ->
                    if (response.isSuccessful) {
                        MentoApplication.prefs.setAccessToken(response.body()!!.tokenDto.accessToken)
                        MentoApplication.prefs.setRefreshToken(response.body()!!.tokenDto.refreshToken)
                        MentoApplication.memberPrefs.setMemberPrefs(response.body()!!.memberDTO)
                        _loginState.value = UiState.Success(response.body())
                    } else {
                        _loginState.value =
                            UiState.Error(Exception(""))
                    }
                }
        }
    }

}