package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.chat.ApplyRejectRequest
import com.minhoi.memento.data.dto.board.BoardContentDto
import com.minhoi.memento.data.dto.board.BoardContentForReceived
import com.minhoi.memento.data.dto.board.BoardListResponse
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.mentoring.MentoringApplyDto
import com.minhoi.memento.data.dto.mentoring.MentoringApplyListDto
import com.minhoi.memento.data.dto.mentoring.MentoringMatchInfo
import com.minhoi.memento.data.dto.mentoring.MentoringReceivedDto
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
) : ViewModel() {

    private var member = MentoApplication.memberPrefs.getMemberPrefs()

    private val applyList = MutableStateFlow<List<MentoringApplyListDto>>(emptyList())

    private val _applyContents =
        MutableStateFlow<List<Pair<MentoringApplyListDto, BoardContentDto>>>(
            emptyList()
        )
    val applyContents: StateFlow<List<Pair<MentoringApplyListDto, BoardContentDto>>> =
        _applyContents.asStateFlow()

    private val _applyContent = MutableStateFlow<UiState<MentoringApplyDto>>(UiState.Empty)
    val applyContent: StateFlow<UiState<MentoringApplyDto>> = _applyContent.asStateFlow()

    private val _mentoringEvent = MutableSharedFlow<MentoringEvent>()
    val mentoringEvent: SharedFlow<MentoringEvent> = _mentoringEvent.asSharedFlow()

    private val _mentorInfo = MutableStateFlow<UiState<List<MentoringMatchInfo>>>(UiState.Empty)
    val mentorInfo: StateFlow<UiState<List<MentoringMatchInfo>>> = _mentorInfo.asStateFlow()

    private val _menteeInfo = MutableStateFlow<UiState<List<MentoringMatchInfo>>>(UiState.Empty)
    val menteeInfo: StateFlow<UiState<List<MentoringMatchInfo>>> = _menteeInfo.asStateFlow()

    private val _imageUploadState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<Boolean>> = _imageUploadState.asStateFlow()

    private val _memberBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val memberBoards: StateFlow<UiState<List<BoardContentDto>>> = _memberBoards.asStateFlow()

    private val _bookmarkBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val bookmarkBoards: StateFlow<UiState<List<BoardContentDto>>> = _bookmarkBoards.asStateFlow()

    private val _memberInfo = MutableLiveData<MemberDTO>()
    val memberInfo: LiveData<MemberDTO> = _memberInfo

    private val _signOutEvent = MutableSharedFlow<Boolean>()
    val signOutEvent: SharedFlow<Boolean> = _signOutEvent.asSharedFlow()

    // 요청받은 멘토링 목록의 상태를 저장하고있는 Flow
    private val memberBoardsFlow = MutableStateFlow<ApiResult<BoardListResponse>>(ApiResult.Empty)
    private val mentoringRequests = MutableStateFlow<ApiResult<List<MentoringReceivedDto>>>(ApiResult.Empty)

    val receivedMentoring =
        combine(memberBoardsFlow, mentoringRequests) { boardsResult, mentoringRequestsResult ->
            Log.d("MypageViewModel", "combine: b:${boardsResult} R:${mentoringRequestsResult}")
            when {
                boardsResult is ApiResult.Empty || mentoringRequestsResult is ApiResult.Empty -> UiState.Empty
                boardsResult is ApiResult.Success && mentoringRequestsResult is ApiResult.Success -> {
                    if (boardsResult.value.content.isEmpty()) {
                        UiState.Error(Throwable("dasd"))
                    }
                    if (currentPage == boardsResult.value.pageInfo.totalPages) {
                        isLastPage = true
                    }

                    val boards = boardsResult.value.content.map {
                        it.toBoardContentForReceived()
                    }
                    val mentoringRequests = mentoringRequestsResult.value
                    UiState.Success(boards.map { board ->
                        val relatedRequests =
                            mentoringRequests.filter { it.boardId == board.boardId }
                        board to relatedRequests
                    })
                }

                boardsResult is ApiResult.Error -> UiState.Error(boardsResult.exception)
                mentoringRequestsResult is ApiResult.Error -> UiState.Error(mentoringRequestsResult.exception)
                boardsResult is ApiResult.Loading || mentoringRequestsResult is ApiResult.Loading -> UiState.Loading
                else -> UiState.Error(Throwable("알 수 없는 오류가 발생하였습니다"))
            }
        }.stateIn(
            initialValue = UiState.Empty,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    // 마이페이지 Activity들에서 paging3 이용하지 않고 page 구현하기 위해 사용되는 변수
    private var currentPage: Int = 1
    var isLastPage: Boolean = false
        private set
    val temp = mutableListOf<BoardContentDto>()

    init {
        getMemberInfo()
    }

    fun getMemberInfo() {
        viewModelScope.launch {
            val response = memberRepository.getMemberInfo(member.id)
            if (response.isSuccessful) {
                _memberInfo.value = response.body()!!.data
            }
        }
    }

    suspend fun getOtherMemberInfo(memberId: Long): MemberDTO? {
        val response = memberRepository.getMemberInfo(memberId)
        return if (response.isSuccessful) {
            Log.d("memberInfo", "getMemberInfo: ${response.body()} ")
            response.body()!!.data
        } else {
            Log.d("memberInfo", "getMemberInfo: ${response.message() + response.code()}")
            null
        }
    }

    fun getApplyList() {
        viewModelScope.launch {
            memberRepository.getApplyList().collect {
                it.handleResponse(
                    onSuccess = { value ->
                        applyList.update { value.data }
                        getApplyBoardContent()
                    },
                    onError = {}
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getApplyBoardContent() {
        val combinedList = mutableListOf<Pair<MentoringApplyListDto, BoardContentDto>>()
        viewModelScope.launch {
            applyList.flatMapConcat { applyDataList ->
                combine(
                    applyDataList.map { applyData ->
                        boardRepository.getBoardContent(applyData.boardId).map { result ->
                            applyData to result
                        }
                    }
                ) { results -> results.toList() }
            }.collect { results ->
                results.forEach { (applyData, boardContentResult) ->
                    boardContentResult.handleResponse(
                        onSuccess = { boardContent ->
                            val combinedData = applyData to boardContent.data.boardDTO
                            combinedList.add(combinedData)
                        },
                        onError = {}
                    )
                }
                _applyContents.update { combinedList }
            }
        }
    }


    fun getApplyInfo(applyId: Long) {
        viewModelScope.launch {
            _applyContent.update { UiState.Loading }
            memberRepository.getApplyInfo(applyId).collectLatest {
                it.handleResponse(
                    onSuccess = { applyInfo ->
                        _applyContent.update { UiState.Success(applyInfo.data) }
                    },
                    onError = { error ->
                        _applyContent.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    private fun mentoringEvent(event: MentoringEvent) {
        viewModelScope.launch {
            _mentoringEvent.emit(event)
        }
    }

    fun acceptApply(applyId: Long) {
        viewModelScope.launch {
            memberRepository.acceptApply(applyId).collect {
                it.handleResponse(
                    onSuccess = {
                        mentoringEvent(MentoringEvent.Accept)
                    },
                    onError = { error ->
                        mentoringEvent(MentoringEvent.Error(error.exception!!))
                    }
                )
            }
        }
    }

    fun rejectApply(applyId: Long, rejectReason: String) {
        viewModelScope.launch {
            memberRepository.rejectApply(
                ApplyRejectRequest(
                    applyId,
                    ApplyRejectRequest.RejectReason(rejectReason)
                )
            ).collect {
                it.handleResponse(
                    onSuccess = {
                        mentoringEvent(MentoringEvent.Reject)
                    },
                    onError = { error ->
                        mentoringEvent(MentoringEvent.Error(error.exception!!))
                    }
                )
            }
        }
    }

    fun getMentorInfo() {
        viewModelScope.launch {
            memberRepository.getMentorInfo().collectLatest { result ->
                result.handleResponse(
                    onSuccess = { matchedMentoringList ->
                        _mentorInfo.update { UiState.Success(matchedMentoringList.data) }
                    },
                    onError = {
                        _mentorInfo.update { UiState.Error(Throwable(it.toString())) }
                    }
                )
            }
        }
    }

    fun getMenteeInfo() {
        viewModelScope.launch {
            memberRepository.getMenteeInfo().collectLatest { result ->
                result.handleResponse(
                    onSuccess = { matchedMentoringList ->
                        _menteeInfo.update { UiState.Success(matchedMentoringList.data) }
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
                    onError = { error ->
                        _imageUploadState.value = UiState.Error(error.exception)
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
                    onError = { error ->
                        _imageUploadState.value = UiState.Error(error.exception)
                    }
                )
            }
        }
    }

    fun getReceivedMentoring() {
        viewModelScope.launch {
            memberRepository.getReceivedList().collectLatest { result ->
                when (result) {
                    is ApiResult.Empty -> {}
                    is ApiResult.Loading -> mentoringRequests.update { ApiResult.Loading }
                    is ApiResult.Success -> {
                        mentoringRequests.update { ApiResult.Success(result.value.data) }
                    }
                    is ApiResult.Error -> mentoringRequests.update { it }
                }
            }
        }
    }

    fun getMemberBoards() {
        viewModelScope.launch {
            memberRepository.getMemberBoards(member.id, currentPage, 4)
                .onStart { _memberBoards.update { UiState.Loading } }
                .collectLatest { result ->
                Log.d("MypageViewModel", "getMemberBoards: $result")
                result.handleResponse(
                    onSuccess = { value ->
                        temp.addAll(value.data.content)
                        if (value.data.pageInfo.totalPages == currentPage) {
                            isLastPage = true
                        }
                        currentPage++
                        _memberBoards.update { currentState ->
                            Log.d("MypageViewModel", "getMemberBoards: $currentState")
                            UiState.Success(temp + value.data.content) }
                    },
                    onError = { error ->
                        _memberBoards.update { UiState.Error(error.exception) }
                    }
                )

                when (result) {
                    is ApiResult.Empty -> {}
                    is ApiResult.Loading -> memberBoardsFlow.update { ApiResult.Loading }
                    is ApiResult.Success -> {
                        memberBoardsFlow.update { ApiResult.Success(result.value.data) }
                    }
                    is ApiResult.Error -> memberBoardsFlow.update { it }
                }
            }
        }
    }

    fun getBookmarkBoards() {
        viewModelScope.launch {
            memberRepository.getBookmarkBoards(currentPage, 10)
                .onStart { _bookmarkBoards.update { UiState.Loading } }
                .collectLatest { result ->
                result.handleResponse(
                    onSuccess = { value ->
                        if (value.data.pageInfo.totalPages == currentPage) {
                            isLastPage = true
                        }
                        currentPage++
                        _bookmarkBoards.update { UiState.Success(value.data.content) }
                    },
                    onError = { error ->
                        _bookmarkBoards.update { UiState.Error(error.exception) }
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

    fun signOut() {
        viewModelScope.launch {
            memberRepository.signOut().collect {
                it.handleResponse(
                    onSuccess = {
                        launch {
                            _signOutEvent.emit(true)
                        }
                    },
                    onError = {
                        launch {
                            _signOutEvent.emit(false)
                        }
                    }
                )
            }
        }
    }

    fun BoardContentDto.toBoardContentForReceived(): BoardContentForReceived {
        return BoardContentForReceived(
            boardId = this.boardId,
            memberName = this.memberName,
            title = this.title,
            school = this.school,
            major = this.major,
            year = this.year,
            introduction = this.introduction,
            target = this.target,
            content = this.content,
            memberId = this.memberId,
            memberImageUrl = this.memberImageUrl,
            representImageUrl = this.representImageUrl,
            isBookmarked = this.isBookmarked,
            isExpanded = false // 기본값 설정
        )
    }

    sealed class MentoringEvent {
        object Accept : MentoringEvent()
        object Reject : MentoringEvent()
        data class Error(val exception: Throwable) : MentoringEvent()
    }
}