package com.minhoi.memento.ui.join

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.repository.JoinRepository
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import kotlinx.coroutines.launch

class JoinViewModel : ViewModel() {
    private val joinRepository = JoinRepository()
    private var _schools = MutableLiveData<List<SchoolDto>>()
    val schools: LiveData<List<SchoolDto>> = _schools

    private var _majors = MutableLiveData<List<MajorDto>>()
    val majors: LiveData<List<MajorDto>> = _majors

    init {
        viewModelScope.launch {
            getSchools()
        }
    }

    private suspend fun getSchools() {
        val schoolsData = joinRepository.getSchools()
        when(schoolsData.isSuccessful) {
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

}