package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MypageViewModel : ViewModel() {

    private val memberRepository = MemberRepository()
    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _applyList = MutableLiveData<List<MentoringApplyDto>>()
    val applyList: LiveData<List<MentoringApplyDto>> = _applyList

    private val _applyContent = MutableLiveData<MentoringApplyDto>()
    val applyContent: LiveData<MentoringApplyDto> = _applyContent

    private val _receivedList = MutableLiveData<List<MentoringReceivedDto>>()
    val receivedList: LiveData<List<MentoringReceivedDto>> = _receivedList

    private val _acceptState = MutableStateFlow<UiState>(UiState.Empty)
    val acceptState: StateFlow<UiState> = _acceptState.asStateFlow()

    private val _rejectState = MutableStateFlow<UiState>(UiState.Empty)
    val rejectState: StateFlow<UiState> = _acceptState.asStateFlow()


    fun getApplyList() {
        viewModelScope.launch {
            member.let { member ->
                val response = memberRepository.getApplyList(member.id)
                if (response.isSuccessful) {
                    _applyList.value = response.body()
                    Log.d("ApplyList", "getApplyListSuccess: ${response.body()}")
                } else {
                    Log.d("ApplyList", "getApplyListFailed: ${response.code()}")
                }
            }
        }
    }

    fun getReceivedList() {
        viewModelScope.launch {
            member.let { member ->
                memberRepository.getReceivedList(member.id)
                .catch { e ->
                    Log.d("ReceivedList", "getReceivedListFailed: ${e.message}")
                }
                .collectLatest {
                    _receivedList.value = it
                    Log.d("ReceivedList", "getReceivedListSuccess: $it")
                }
            }
        }
    }

    fun acceptApply(applyId: Long) {
        viewModelScope.launch {
            _acceptState.update { UiState.Loading }
            val response = memberRepository.acceptApply(applyId)
            if (response.isSuccessful) {
                _acceptState.update { UiState.Success(response.body())}
            } else {
                _acceptState.update { UiState.Error(null) }
            }
        }
    }

    fun rejectApply(applyId: Long) {
        _rejectState.update { UiState.Loading }
        viewModelScope.launch {
            val response = memberRepository.rejectApply(applyId)
            if (response.isSuccessful) {
                _rejectState.update { UiState.Success(response.body())  }
            }
            else {
                _rejectState.update { UiState.Error(null) }
                }
            }
        }

    private suspend fun getApplyState(): List<MentoringMatchInfo> {
        val response = memberRepository.getMatchedMentoringInfo(member.id)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            return emptyList()
        }
    }

    fun selectApplyContent(applyContent: MentoringApplyDto) {
        _applyContent.value = applyContent
    }
}