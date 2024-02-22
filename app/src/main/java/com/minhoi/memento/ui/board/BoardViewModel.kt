package com.minhoi.memento.ui.board

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.repository.BoardRepository
import com.minhoi.memento.utils.DayOfWeek
import kotlinx.coroutines.launch
import java.time.LocalDate

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    val getBoardsStream = boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)

    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    init {
        _isAvailableDay.value = false
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