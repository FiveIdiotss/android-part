package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentForReceived
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.repository.BoardRepository
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
    private val boardRepository = BoardRepository()
    private var member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _applyList = MutableLiveData<List<Pair<MentoringApplyListDto, ApplyStatus>>>()
    val applyList: LiveData<List<Pair<MentoringApplyListDto, ApplyStatus>>> = _applyList

    private val _applyContent = MutableStateFlow<UiState<MentoringApplyDto>>(UiState.Empty)
    val applyContent: StateFlow<UiState<MentoringApplyDto>> = _applyContent.asStateFlow()

    private val _boardsWithReceivedMentoring = MutableStateFlow<UiState<List<Map<BoardContentForReceived, List<MentoringReceivedDto>>>>>(UiState.Empty)
    val boardsWithReceivedMentoring: StateFlow<UiState<List<Map<BoardContentForReceived, List<MentoringReceivedDto>>>>> =
        _boardsWithReceivedMentoring.asStateFlow()

    private val _acceptState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val acceptState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _rejectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val rejectState: StateFlow<UiState<Boolean>> = _acceptState.asStateFlow()

    private val _mentorInfo = MutableStateFlow<UiState<List<MentoringMatchInfo>>>(UiState.Empty)
    val mentorInfo: StateFlow<UiState<List<MentoringMatchInfo>>> = _mentorInfo.asStateFlow()

    private val _menteeInfo = MutableStateFlow<UiState<List<MentoringMatchInfo>>>(UiState.Empty)
    val menteeInfo: StateFlow<UiState<List<MentoringMatchInfo>>> = _menteeInfo.asStateFlow()

    private val _imageUploadState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<Boolean>> = _imageUploadState.asStateFlow()

    private val _memberBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val memberBoards: LiveData<UiState<List<BoardContentDto>>> = _memberBoards.asLiveData()

    private val _bookmarkBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val bookmarkBoards: LiveData<UiState<List<BoardContentDto>>> = _bookmarkBoards.asLiveData()

    private val _memberInfo = MutableLiveData<MemberDTO>()
    val memberInfo: LiveData<MemberDTO> = _memberInfo

    init {
        getMemberInfo()
    }
    fun getMemberInfo() {
        viewModelScope.launch {
            val response = memberRepository.getMemberInfo(member.id)
            if (response.isSuccessful) {
                _memberInfo.value = response.body()!!
            }
        }
    }

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
                val applyStateIds = memberRepository.getMentorInfo(member.id).extractSuccess().map { it.applyId }
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

    suspend fun getMentorInfo() {
        viewModelScope.launch {
            memberRepository.getMentorInfo(member.id).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { matchedMentoringList ->
                        _mentorInfo.update { UiState.Success(matchedMentoringList) }
                    },
                    onError = {
                        _mentorInfo.update { UiState.Error(Throwable(it.toString())) }
                    }
                )
            }
        }
    }

    suspend fun getMenteeInfo() {
        viewModelScope.launch {
            memberRepository.getMenteeInfo(member.id).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { matchedMentoringList ->
                        _menteeInfo.update { UiState.Success(matchedMentoringList) }
                    },
                    onError = {
                        _menteeInfo.update { UiState.Error(Throwable(it.toString())) }
                    }
                )
            }
        }
    }

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
            val bookmarkBoards = memberRepository.getBookmarkBoards().extractSuccess()
            member.let { member ->
                memberRepository.getMemberBoards(member.id).collectLatest {
                    it.handleResponse(
                        // 자신의 북마크 목록의 boardId와 내가 작성한 글의 boardId 비교하여 북마크 여부 확인
                        onSuccess = { boards ->
                            val memberBoards = boards.map { boardContentDto ->
                                if (bookmarkBoards.any { bookmarkBoard ->
                                        bookmarkBoard.boardId == boardContentDto.boardId
                                    }) {
                                    boardContentDto.apply { isBookmarked = true }
                                } else {
                                    boardContentDto
                                }
                            }
                            _memberBoards.update { UiState.Success(memberBoards) }
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

    fun getBookmarkBoards() {
        viewModelScope.launch {
            _bookmarkBoards.update { UiState.Loading }
            memberRepository.getBookmarkBoards().collectLatest {
                it.handleResponse(
                    onSuccess = { bookmarkBoards ->
                        bookmarkBoards.map {
                            it.isBookmarked = true
                        }
                        _bookmarkBoards.update { UiState.Success(bookmarkBoards) }
                    },
                    onError = { errorMsg ->
                        _bookmarkBoards.update { UiState.Error(Throwable(errorMsg)) }
                    }
                )
            }
        }
    }

    fun executeBookmarkInList(boardId: Long, bookmarkState: Boolean) {
        viewModelScope.launch {
            val s = when (bookmarkState) {
                true -> boardRepository.executeUnBookmark(boardId)
                false -> boardRepository.executeBookmark(boardId)
            }
            s.collectLatest {
                it.handleResponse(
                    onSuccess = {

                    },
                    onError = { errorMsg ->

                    }
                )
            }
        }
    }

}