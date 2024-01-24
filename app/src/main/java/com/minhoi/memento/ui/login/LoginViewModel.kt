package com.minhoi.memento.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minhoi.memento.repository.LoginRepository


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

}