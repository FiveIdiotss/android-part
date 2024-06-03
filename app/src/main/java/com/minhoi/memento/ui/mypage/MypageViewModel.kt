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
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.member.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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

    private val _applyList = MutableLiveData<List<Pair<MentoringApplyListDto, ApplyStatus>>>()
    val applyList: LiveData<List<Pair<MentoringApplyListDto, ApplyStatus>>> = _applyList

    private val _applyContent = MutableStateFlow<UiState<MentoringApplyDto>>(UiState.Empty)
    val applyContent: StateFlow<UiState<MentoringApplyDto>> = _applyContent.asStateFlow()

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
    val memberBoards: StateFlow<UiState<List<BoardContentDto>>> = _memberBoards.asStateFlow()

    private val _bookmarkBoards = MutableStateFlow<UiState<List<BoardContentDto>>>(UiState.Empty)
    val bookmarkBoards: StateFlow<UiState<List<BoardContentDto>>> = _bookmarkBoards.asStateFlow()

    private val _memberInfo = MutableLiveData<MemberDTO>()
    val memberInfo: LiveData<MemberDTO> = _memberInfo

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
                        _isLastPage.value = true
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
                    onError = { error ->
                        _applyContent.update { UiState.Error(error.exception) }
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
            isBookmarked = this.isBookmarked,
            isExpanded = false // 기본값 설정
        )
    }

}