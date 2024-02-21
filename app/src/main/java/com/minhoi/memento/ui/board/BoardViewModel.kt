package com.minhoi.memento.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.repository.BoardRepository
import com.minhoi.memento.utils.DayOfWeek
import kotlinx.coroutines.launch

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    val getBoardsStream = boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)

    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

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

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val selectedDay = DayOfWeek.getDayOfWeek(year, month, day)
        _isAvailableDay.value = post.value?.availableDays!!.contains(selectedDay)
    }
}