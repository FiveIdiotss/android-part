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

    private val _boardList = MutableStateFlow<UiState<PagingData<BoardContentDto>>>(UiState.Empty)
    val boardList: StateFlow<UiState<PagingData<BoardContentDto>>> = _boardList.asStateFlow()
    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

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
            val response = boardRepository.getBoardContent(boardId)
            if (response.isSuccessful) {
                _post.value = response.body()
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
        _isAvailableDay.value = post.value?.availableDays!!.contains(selectedDay)
    }

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

}