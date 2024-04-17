package com.minhoi.memento.ui.board

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.repository.BoardRepository
import com.minhoi.memento.data.model.DayOfWeek
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.extractSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()
    private val memberRepository = MemberRepository()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    private val _boardContent = MutableStateFlow<UiState<BoardContentDto>>(UiState.Loading)
    val boardContent: StateFlow<UiState<BoardContentDto>> = _boardContent.asStateFlow()

    private val _boardList = MutableStateFlow<UiState<PagingData<BoardContentDto>>>(UiState.Empty)
    val boardList: StateFlow<UiState<PagingData<BoardContentDto>>> = _boardList.asStateFlow()
    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

    private val _bookmarkState = MutableStateFlow<UiState<Long>>(UiState.Empty)
    val bookmarkState: StateFlow<UiState<Long>> = _bookmarkState.asStateFlow()

    private val _unBookmarkState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val unBookmarkState: StateFlow<UiState<Boolean>> = _unBookmarkState.asStateFlow()

    private val _boardBookmarkState = MutableLiveData<Boolean>()
    val boardBookmarkState: LiveData<Boolean> = _boardBookmarkState

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    init {
        _isAvailableDay.value = false
        getBoardStream()
    }

    fun getBoardStream() {
        viewModelScope.launch {
            memberRepository.getBookmarkBoards().collectLatest { bookmarkBoards ->
                if (bookmarkBoards is ApiResult.Success) {
                    boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)
                        .collectLatest { boards ->
                            val transformedPagingData = boards.map { boardContentDto ->
                                // 즐겨찾기 목록에 있는지 확인하고, 상태를 업데이트합니다.
                                if (bookmarkBoards.value.any { bookmarkBoard ->
                                        bookmarkBoard.boardId == boardContentDto.boardId
                                    }) {
                                    boardContentDto.apply { isBookmarked = true }
                                } else {
                                    boardContentDto
                                }
                            }
                            _boardList.update { UiState.Success(transformedPagingData) }
                        }
                } else {
                    _boardList.update { UiState.Error(Throwable("BookmarkBoards is null")) }
                }
            }
        }
    }

    fun getBoardContent(boardId: Long) {
        viewModelScope.launch {
            val bookmarkBoards = memberRepository.getBookmarkBoards().extractSuccess()
            boardRepository.getBoardContent(boardId).collectLatest {
                it.handleResponse(
                    onSuccess = { response ->
                        _post.value = response
                        val isBookmarked = bookmarkBoards.any { bookmarkBoard ->
                            bookmarkBoard.boardId == response.boardDTO.boardId
                        }
                        _boardBookmarkState.value = isBookmarked
                        // bookmarkState 업데이트
                        val boardContent = response.boardDTO.copy(isBookmarked = isBookmarked)
                        _boardContent.update { UiState.Success(boardContent) }
                    },
                    onError = { errorMsg ->
                        _boardContent.update { UiState.Error(Throwable(errorMsg))}
                    }
                )
            }
        }
    }

    fun setSelectedTime(time: String) {
        selectedTime = time
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val selectedDay = DayOfWeek.getDayOfWeek(year, month, day)
        selectedDate = LocalDate.of(year, month+1, day).toString()
        selectedTime = ""
        val isAvailable = post.value?.availableDays?.contains(selectedDay) ?: false
        _isAvailableDay.value = isAvailable
    }

    fun getSelectedTime() = selectedTime
    fun getSelectedDate() = selectedDate

    fun applyMentoring() {
        viewModelScope.launch {
            val response = boardRepository.applyMentoring(_post.value!!.boardDTO.boardId,
                MentoringApplyRequest("content", selectedDate!!, selectedTime))

            if (response.isSuccessful) {
                Log.d("APPLYMENTORING", "applyMentoring: 성공")
            }
            else {
                // 실패 처리
                Log.d("APPLYMENTORING", "applyMentoring: 실패 ${response.code()}")
            }
        }
    }

    fun executeBookmark(boardId: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            _bookmarkState.update { UiState.Loading }
            val s = when (isBookmarked) {
                true -> boardRepository.executeUnBookmark(boardId)
                false -> boardRepository.executeBookmark(boardId)
            }
            s.collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _bookmarkState.update { UiState.Success(boardId) }
                        _boardBookmarkState.value = !_boardBookmarkState.value!!
                    },
                    onError = {
                        _bookmarkState.update { UiState.Error(Throwable(it.toString())) }
                    }
                )
            }
        }
    }
}