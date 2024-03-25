package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.data.model.ApplyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MypageViewModel : ViewModel() {

    private val memberRepository = MemberRepository()
    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _applyList = MutableLiveData<List<Pair<MentoringApplyDto, ApplyStatus>>>()
    val applyList: LiveData<List<Pair<MentoringApplyDto, ApplyStatus>>> = _applyList

    private val _applyContent = MutableLiveData<MentoringApplyDto>()
    val applyContent: LiveData<MentoringApplyDto> = _applyContent

    private val _receivedList = MutableLiveData<List<MentoringReceivedDto>>()
    val receivedList: LiveData<List<MentoringReceivedDto>> = _receivedList

    private val _acceptState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val acceptState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _rejectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val rejectState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _matchedMentoringList = MutableLiveData<List<MentoringMatchInfo>>()
    val matchedMentoringList: LiveData<List<MentoringMatchInfo>> = _matchedMentoringList

    private val _imageUploadState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<Boolean>> = _imageUploadState.asStateFlow()

    suspend fun getOtherMemberInfo(memberId: Long): MemberDTO? {
        val response = memberRepository.getMemberInfo(memberId)
        return if (response.isSuccessful) {
            Log.d("memberInfo", "getMemberInfo: ${response.body()} ")
            response.body()
        } else {
            Log.d("memberInfo", "getMemberInfo: ${response.message() + response.code()}")
            null
        }
    }

    fun getApplyList() {
        viewModelScope.launch {
            member.let { member ->
                getApplyState()
                val applyStateIds = _matchedMentoringList.value?.map { it.applyId }?.toSet()

                val response = memberRepository.getApplyList(member.id)
                if (response.isSuccessful) {
                    val applyList = response.body() ?: throw Exception("ApplyList is null")
                    val applyListWithState = applyList.map { applyItem ->
                        when (applyItem.applyState) {
                            "HOLDING" -> Pair(applyItem, ApplyStatus.ACCEPTANCE_PENDING)
                            else -> {
                                if (applyItem.applyId in applyStateIds!!) {
                                    Pair(applyItem, ApplyStatus.ACCEPTED)
                                } else {
                                    Pair(applyItem, ApplyStatus.REJECTED)
                                }
                            }
                        }
                    }
                    _applyList.value = applyListWithState
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
                _acceptState.update { UiState.Success(true) }
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
                _rejectState.update { UiState.Success(true) }
            } else {
                _rejectState.update { UiState.Error(null) }
            }
        }
    }

    private suspend fun getApplyState() {
        val response = memberRepository.getMatchedMentoringInfo(member.id)
        response.collectLatest { res ->
            res.handleResponse(
                onSuccess = { matchedMentoringList ->
                    // Update _matchedMentoringList with the received data
                    _matchedMentoringList.value = matchedMentoringList
                },
                onError = { Log.d("MatchedMentoring", "getMatchedMentoringFailed: ${it}") }
            )
        }
    }

    suspend fun getMatchedMentoring() {
        viewModelScope.launch {
            memberRepository.getMatchedMentoringInfo(member.id).collectLatest {
                it.handleResponse(
                    onSuccess = { matchedMentoringList ->
                        _matchedMentoringList.value = matchedMentoringList
                    },
                    onError = { Log.d("MatchedMentoring", "getMatchedMentoringFailed: ${it}") }
                )
            }
        }
    }

    fun selectApplyContent(applyContent: MentoringApplyDto) {
        _applyContent.value = applyContent
    }

    fun getMemberInfo() = member

    fun uploadProfileImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            _imageUploadState.value = UiState.Loading
            memberRepository.uploadProfileImage(imagePart).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _imageUploadState.value = UiState.Success(true)
                    },
                    onError = {
                        _imageUploadState.value = UiState.Error(Throwable(it))
                    }
                )
            }
        }
    }

    fun setDefaultProfileImage() {
        viewModelScope.launch {
            memberRepository.setDefaultProfileImage().collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _imageUploadState.value = UiState.Success(true)
                    },
                    onError = {
                        _imageUploadState.value = UiState.Error(Throwable(it))
                    }
                )
            }
        }
    }
}