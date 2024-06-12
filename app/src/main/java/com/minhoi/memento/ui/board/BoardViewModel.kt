package com.minhoi.memento.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.model.DayOfWeek
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    private val _boardContent = MutableStateFlow<UiState<BoardContentDto>>(UiState.Loading)
    val boardContent: StateFlow<UiState<BoardContentDto>> = _boardContent.asStateFlow()

    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

    private val _boardBookmarkState = MutableLiveData<Boolean>()
    val boardBookmarkState: LiveData<Boolean> = _boardBookmarkState

    private val _applyState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val applyState: StateFlow<UiState<Boolean>> = _applyState.asStateFlow()

    private val _isMyPost = MutableStateFlow<Boolean>(false)
    val isMyPost: StateFlow<Boolean> = _isMyPost.asStateFlow()

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    init {
        _isAvailableDay.value = false
    }

    fun getBoardContent(boardId: Long) {
        viewModelScope.launch {
            boardRepository.getBoardContent(boardId).collectLatest {
                it.handleResponse(
                    onSuccess = { value ->
                        _post.value = value.data
                        _boardContent.update { UiState.Success(value.data.boardDTO) }
                        if (value.data.boardDTO.memberId == member.id) {
                            _isMyPost.update { true }
                        }
                    },
                    onError = { error ->
                        _boardContent.update { UiState.Error(error.exception) }
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
        selectedDate = LocalDate.of(year, month + 1, day).toString()
        selectedTime = ""
        val isAvailable = post.value?.availableDays?.contains(selectedDay) ?: false
        _isAvailableDay.value = isAvailable
    }

    fun getSelectedTime() = selectedTime
    fun getSelectedDate() = selectedDate

    fun applyMentoring() {
        viewModelScope.launch {
            boardRepository.applyMentoring(
                _post.value!!.boardDTO.boardId,
                MentoringApplyRequest("content", selectedDate, selectedTime)
            ).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _applyState.update { UiState.Success(true) }
                    },
                    onError = { error ->
                        _applyState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }
}