package com.minhoi.memento.ui.join

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.repository.JoinRepository
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.MemberDto
import com.minhoi.memento.data.dto.SchoolDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = JoinViewModel::class.simpleName

class JoinViewModel : ViewModel() {
    private val joinRepository = JoinRepository()

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

    private var _year = MutableLiveData<String>()
    val year: LiveData<String> = _year

    private var _majorId = MutableLiveData<Int>()
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
        _verificationState.postValue(false)
        _emailAndSchoolVerificationState.postValue(false)
        viewModelScope.launch {
            getSchools()
        }
    }

    private suspend fun getSchools() {
        val schoolsData = joinRepository.getSchools()
        when (schoolsData.isSuccessful) {
            true -> _schools.value = schoolsData.body()
            else -> {}
        }
    }

    suspend fun getMajorsBySchool(name: String) {
        viewModelScope.launch {
            val majorsData = joinRepository.getMajors(name)
            when (majorsData.isSuccessful) {
                true -> {
                    _majors.value = majorsData.body()
                }

                else -> {}
            }
        }
    }

    suspend fun join() {
        val member = MemberDto(
            _email.value.toString(),
            _name.value.toString(),
            _password.value.toString(),
            year.value!!.toInt(),
            "MALE",
            _school.value.toString(),
            _majorId.value!!
        )

        Log.d("memberDto", "join: ${member.toString()} ")

        viewModelScope.launch {
            val response = joinRepository.join(member)
            if (response.isSuccessful) {
                Log.d("hahahahaha", "${response.body()}")
            } else {
                Log.d("hahahahaha", "${response.body()}")
            }
        }
    }

    suspend fun verifySchoolAndEmail() {
        val request = EmailVerificationRequest(
            _email.value.toString(),
            _school.value.toString()
        )

        viewModelScope.launch {
            val response = joinRepository.verifyEmail(request)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && !response.body()!!.contains("false")) {
                    _emailAndSchoolVerificationState.postValue(true)
                }
            }
        }
    }

    suspend fun verifyCode() {
        val request = VerifyCodeRequest(
            _email.value.toString(),
            _school.value.toString(),
            _verificationCode.value.toString().toInt()
        )

        viewModelScope.launch {
            val response = joinRepository.verifyCode(request)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && !response.body()!!.contains("false")) {
                    _verificationState.postValue(true)
                }
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