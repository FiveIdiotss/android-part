package com.minhoi.memento.ui.join

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.repository.join.JoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = JoinViewModel::class.simpleName

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinRepository: JoinRepository
) : ViewModel() {

    private var _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> = _verificationCode

    private var _verificationState = MutableLiveData<Boolean>()
    val verificationState: LiveData<Boolean> = _verificationState

    private var _emailAndSchoolVerificationState = MutableLiveData<Boolean>()
    val emailAndSchoolVerificationState: LiveData<Boolean> = _emailAndSchoolVerificationState

    private var _schools = MutableLiveData<List<SchoolDto>>()
    val schools: LiveData<List<SchoolDto>> = _schools

    private var _school = MutableLiveData<String>()
    val school: LiveData<String> = _school

    var year = MutableLiveData<String>()

    private var _majorId = MutableLiveData<Int>(-1)
    val majorId: LiveData<Int> = _majorId

    private var _majors = MutableLiveData<List<MajorDto>>()
    val majors: LiveData<List<MajorDto>> = _majors

    private var _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private var _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private var _passwordCheck = MutableLiveData<String>()
    val passwordCheck: LiveData<String> = _passwordCheck

    private var _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private var _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    init {
        _verificationState.value = false
        _emailAndSchoolVerificationState.value = false
        viewModelScope.launch {
            getSchools()
        }
    }

    private fun getSchools() {
        viewModelScope.launch {
            joinRepository.getSchools().collect {
                it.handleResponse(
                    onSuccess = { value ->
                        _schools.value = value.data
                    },
                    onError = {

                    }
                )
            }
        }
    }

    fun getMajorsBySchool(name: String) {
        viewModelScope.launch {
            joinRepository.getMajors(name).collect {
                it.handleResponse(
                    onSuccess = { value ->
                        _majors.value = value.data
                    },
                    onError = {}
                )
            }
        }
    }

    fun join() {
        val member = CreateMemberRequest(
            _email.value.toString(),
            _name.value.toString(),
            _password.value.toString(),
            year.value!!.toInt(),
            "MALE",
            _school.value.toString(),
            _majorId.value!!
        )

        viewModelScope.launch {
            joinRepository.join(member).collect {

            }
        }
    }

    fun verifySchoolAndEmail() {
        val request = EmailVerificationRequest(
            _email.value.toString(),
            _school.value.toString()
        )

        viewModelScope.launch {
            joinRepository.verifyEmail(request).collect {
                it.handleResponse(
                    onSuccess = { value ->
                        _emailAndSchoolVerificationState.value = true
                    },
                    onError = {
                        Log.d(TAG, "verifySchoolAndEmail: {${it.exception!!.message}")
                    }
                )
            }
        }
    }

    fun verifyCode() {
        viewModelScope.launch {
            val request = VerifyCodeRequest(
                _email.value.toString(),
                _school.value.toString(),
                _verificationCode.value.toString().toInt()
            )
            joinRepository.verifyCode(request).collect {
                it.handleResponse(
                    onSuccess = { value ->
                        _verificationState.value = true
                    },
                    onError = {}
                )
            }
        }
    }

    fun onVerifyCodeChanged(text: CharSequence) {
        _verificationCode.value = text.toString()
    }

    fun onSchoolTextChanged(text: CharSequence) {
        _school.value = text.toString()
    }

    fun onEmailTextChanged(text: CharSequence) {
        _email.value = text.toString()
    }

    fun onPasswordTextChanged(text: CharSequence) {
        _password.value = text.toString()
    }

    fun onPasswordCheckTextChanged(text: CharSequence) {
        _passwordCheck.value = text.toString()
    }

    fun onNameTextChanged(text: CharSequence) {
        _name.value = text.toString()
    }

    fun setMajorId(id: Int) {
        _majorId.value = id
    }


}