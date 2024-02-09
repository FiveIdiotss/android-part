package com.minhoi.memento.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.repository.BoardRepository
import kotlinx.coroutines.launch

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()

    private val _post = MutableLiveData<BoardContentDto>()
    val post: LiveData<BoardContentDto> = _post

    val getBoardsStream = boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)

    fun getBoardContent(boardId: Long) {
        viewModelScope.launch {
            val response = boardRepository.getBoardContent(boardId)
            if (response.isSuccessful) {
                _post.value = response.body()?.boardDTO
            }
        }
    }
}