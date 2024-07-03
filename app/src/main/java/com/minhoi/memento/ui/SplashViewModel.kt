package com.minhoi.memento.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.repository.member.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun checkLoginState(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val refreshToken = MentoApplication.prefs.getRefreshToken("")
            if (refreshToken.isNullOrEmpty()) {
                onFailure()
                return@launch
            }
            memberRepository.checkLoginState(refreshToken).collect {
                it.handleResponse(
                    onSuccess = {
                        MentoApplication.prefs.setAccessToken(it.data.accessToken)
                        MentoApplication.prefs.setRefreshToken(it.data.refreshToken)
                        _loginState.update { LoginState.Success }
                        onSuccess()
                    },
                    onError = { error ->
                        Log.d(TAG, "checkLoginState: ${error.exception!!.message}")
                        if (error.exception.message == EXPIRED_REFRESH_TOKEN_MESSAGE) {
                            _loginState.update { LoginState.Failure }
                        } else {
                            _loginState.update { LoginState.Error }
                        }
                        onFailure()
                    }
                )
            }
        }
    }

    sealed class LoginState {
        object Success : LoginState()
        object Failure : LoginState()
        object Error : LoginState()
        object Loading : LoginState()
    }

    companion object {
        private const val TAG = "SplashViewModel"
        private const val EXPIRED_REFRESH_TOKEN_MESSAGE = "유효하지 않은 토큰입니다."
    }
}