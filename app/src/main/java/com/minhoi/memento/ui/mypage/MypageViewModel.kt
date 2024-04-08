package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentForReceived
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.utils.extractSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MypageViewModel : ViewModel() {

    private val memberRepository = MemberRepository()
    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _applyList = MutableLiveData<List<Pair<MentoringApplyDto, ApplyStatus>>>()
    val applyList: LiveData<List<Pair<MentoringApplyDto, ApplyStatus>>> = _applyList

    private val _applyContent = MutableStateFlow<UiState<MentoringApplyDto>>(UiState.Empty)
    val applyContent: StateFlow<UiState<MentoringApplyDto>> = _applyContent.asStateFlow()

    private val _boardsWithReceivedMentoring = MutableStateFlow<UiState<List<Map<BoardContentForReceived, List<MentoringReceivedDto>>>>>(UiState.Empty)
    val boardsWithReceivedMentoring: StateFlow<UiState<List<Map<BoardContentForReceived, List<MentoringReceivedDto>>>>> =
        _boardsWithReceivedMentoring.asStateFlow()

    private val _acceptState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val acceptState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _rejectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val rejectState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _matchedMentoringList = MutableLiveData<List<MentoringMatchInfo>>()
    val matchedMentoringList: LiveData<List<MentoringMatchInfo>> = _matchedMentoringList

    private val _imageUploadState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<Boolean>> = _imageUploadState.asStateFlow()

    private val _memberBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val memberBoards: StateFlow<UiState<List<BoardContentDto>>> = _memberBoards.asStateFlow()

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

    fun getApplyInfo(applyId: Long) {
        viewModelScope.launch {
            _applyContent.update { UiState.Loading }
            memberRepository.getApplyInfo(applyId).collectLatest {
                it.handleResponse(
                    onSuccess = { applyInfo ->
                        _applyContent.update { UiState.Success(applyInfo) }
                    },
                    onError = { errorMsg ->
                        _applyContent.update { UiState.Error(Throwable(errorMsg)) }
                    }
                )
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

    fun getMemberBoards() {
        viewModelScope.launch {
            _memberBoards.update { UiState.Loading }
            member.let { member ->
                memberRepository.getMemberBoards(member.id).collectLatest {
                    it.handleResponse(
                        onSuccess = { boards ->
                            _memberBoards.update { UiState.Success(boards) }
                        },
                        onError = { errorMsg ->
                            _memberBoards.update { UiState.Error(Throwable(errorMsg)) }
                        }
                    )
                }
            }
        }
    }

    fun getBoardsWithReceivedMentoring() {
        viewModelScope.launch {
            // loading
            _boardsWithReceivedMentoring.update { UiState.Loading }
            val memberBoards = memberRepository.getMemberBoards(member.id).extractSuccess().map {
                Log.d("MypageVIewmodel", "memberBoards = $it")
                BoardContentForReceived(
                    it.boardId,
                    it.memberName,
                    it.title,
                    it.school,
                    it.major,
                    it.year,
                    it.introduction,
                    it.target,
                    it.content,
                    it.memberId,
                    it.isBookmarked,
                    false
                )
            }
            val receivedList = memberRepository.getReceivedList(member.id).extractSuccess()
            if (memberBoards == null || receivedList == null) {
                _boardsWithReceivedMentoring.update { UiState.Error(Throwable()) }
                return@launch
            }
            val boardsWithMentoringReceived =
                mutableListOf<Map<BoardContentForReceived, List<MentoringReceivedDto>>>()
            memberBoards.forEach {
                val receivedContent = receivedList.filter { received -> received.boardId == it.boardId }
                boardsWithMentoringReceived.add(mapOf(it to receivedContent))
            }
            _boardsWithReceivedMentoring.update { UiState.Success(boardsWithMentoringReceived) }
        }
    }
}